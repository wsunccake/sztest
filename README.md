# sztest


## download sztest

```bash
# use SSH
~ $ git clone git@github.com:wsunccake/sztest.git

# use HTTPS
~ $ git clone https://github.com/wsunccake/sztest.git
```


## setup environment variable

```bash
~ $ export SZTEST_HOME=~/sztest
```


## use api util

```bash
# setup env
~ $ export API_VERSION=v9_1
~ $ source $SZTEST_HOME/conf/default/setup_var.sh
~ $ source $SZTEST_HOME/util/api_util.sh
~ $ export SZ_IP=<sz_ip>
~ $ setup_api_var

# show env
~ $ show_api_var

# run api
~ $ pubapi_login $SZ_USERNAME $SZ_PASSWORD
~ $ get_all_domain
~ $ pubapi_logout
```

