#!/usr/bin/env python3
import argparse
import sys

import requests
import re
import logging

from bs4 import BeautifulSoup

requests.packages.urllib3.disable_warnings()


class Lbs:
    def __init__(self, ip, username, password):
        self.ip = ip
        self.username = username
        self.password = password

        self.authenticity_token = None
        self.base_url = f'https://{self.ip}'
        self.session = requests.Session()
        self.csrf_param = None
        self.csrf_token = None

        self.get_csrf()

    def get_csrf(self):
        response = self.session.get(url=f'{self.base_url}/sessions/new', verify=False, allow_redirects=False)
        match_all = re.findall(r'<meta.+?>', response.text)

        for m in match_all:
            if m.find('name="csrf-param"') > 0:
                s = re.search(r'content=\"(.*?)\"', m)
                if s is None:
                    raise RuntimeError('no found csrf')
                self.csrf_param = s.group(1)

            if m.find('name="csrf-token"') > 0:
                s = re.search(r'content=\"(.*?)\"', m)
                if s is None:
                    raise RuntimeError('no found csrf')
                self.csrf_token = s.group(1)

        if self.csrf_token is None:
            raise RuntimeError('No found csrf token')

    def login(self):
        url = f'{self.base_url}/sessions'
        data = {
            self.csrf_param: self.csrf_token,
            'email': self.username,
            'password': self.password
        }
        header = {'Content-Type': 'application/x-www-form-urlencoded'}

        response = self.session.post(url=url, data=data, headers=header, verify=False, allow_redirects=False)

        logging.info('*** LOGIN ***')
        logging.debug(response.status_code)
        if response.status_code != 303:
            raise RuntimeError('Fail to login')
        logging.debug(response.headers)
        logging.debug(response.text)

    def logout(self):
        url = f'{self.base_url}/sessions'
        data = {
            self.csrf_param: self.csrf_token,
            '_method': 'delete'
        }
        header = {'Content-Type': 'application/x-www-form-urlencoded'}

        response = self.session.post(url=url, data=data, headers=header, verify=False, allow_redirects=False)

        logging.info('*** LOGOUT ***')
        logging.debug(response.status_code)
        if response.status_code != 303:
            raise RuntimeError('Fail to logout')
        logging.debug(response.headers)
        logging.debug(response.text)

    def get_venues_list(self):
        url = f'{self.base_url}/admin/venues'
        response = self.session.get(url=url, verify=False)

    #        print(response.status_code)

    def get_venue_password(self, venue_name):
        url = f'{self.base_url}/admin/venues/{venue_name}/edit_config?'
        response = self.session.get(url=url, verify=False)
        soup = BeautifulSoup(response.text, 'html.parser')
        results = soup.find_all(attrs={'id': 'mqtt_psk_field'})
        password = None
        if len(results) < 0:
            raise RuntimeWarning('No found password')
        else:
            password = results[0].text.strip()
        return password

    def new_venue(self, venue_name):
        url = f'{self.base_url}/admin/venues'
        data = {
            self.csrf_param: self.csrf_token,
            'venue[venue_id]': venue_name,
            'venue[name]': venue_name,
            'venue[locality_mode]': 'assisted',
            'venue[time_zone_id]': 'Asia/Taipei'
        }
        header = {'Content-Type': 'application/x-www-form-urlencoded'}

        response = self.session.post(url=url, data=data, headers=header, verify=False, allow_redirects=False)

        logging.info('*** New Venue ***')
        logging.debug(response.status_code)
        if response.status_code != 303:
            raise RuntimeError('Fail to new venue')
        logging.debug(response.headers)
        logging.debug(response.text)

    def delete_venue(self, venue_name):
        url = f'{self.base_url}/admin/venues/{venue_name}'
        data = {
            self.csrf_param: self.csrf_token,
            '_method': 'delete'
        }
        header = {'Content-Type': 'application/x-www-form-urlencoded'}

        response = self.session.post(url=url, data=data, headers=header, verify=False, allow_redirects=False)

        logging.info('*** Delete Venue ***')
        logging.debug(response.status_code)
        if response.status_code != 303:
            raise RuntimeWarning('Fail to delete venue')
        logging.debug(response.headers)
        logging.debug(response.text)


def logging_config(output_file, debug_level):
    if output_file is None:
        handler = logging.StreamHandler(sys.stdout)
    else:
        handler = logging.FileHandler(output_file, 'w', 'utf-8')

    debug_dict = {
        'debug': logging.DEBUG,
        'info': logging.INFO,
        'warn': logging.WARN,
        'error': logging.ERROR
    }
    if debug_level not in debug_dict:
        debug_level = 'info'

    logging.basicConfig(
        level=debug_dict[debug_level],
        format='%(asctime)s %(levelname)s %(message)s',
        datefmt='%Y-%m-%d %H:%M',
        handlers=[handler]
    )


def argument_parser():
    parser = argparse.ArgumentParser(description='LBS API')

    parser.add_argument('-u', '--username', help='username', required=True)
    parser.add_argument('-p', '--password', help='password', required=True)
    parser.add_argument('-s', '--server', help='lbs server', required=True)
    parser.add_argument('-a', '--action', help='action: create, psk', required=True)
    parser.add_argument('-l', '--level', help='debug level: info, debug', required=False)

    return parser.parse_args()


if __name__ == '__main__':
    args = argument_parser()

    server_ip = args.server
    username = args.username
    password = args.password
    action = args.action

    logging_config(None, args.level)

    lbs = Lbs(server_ip, username, password)
    lbs.login()

    if action == 'create':
        for i in range(1, 1025):
            lbs_name = f'lbs-{i}'
            print(lbs_name)
            lbs.new_venue(lbs_name)
    elif action == 'psk':
        for i in range(1, 1025):
            lbs_name = f'lbs-{i}'
            p = lbs.get_venue_password(lbs_name)
            print(lbs_name, p)
    else:
        pass

    lbs.logout()

