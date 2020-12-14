import fetch from "node-fetch";

process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";

var apiVersion = process.env.API_VERSION;
var userName = process.env.SZ_USERNAME;
var password = process.env.SZ_PASSWORD;

var COOKIE = '';
const base_url = `https://${process.env.SZ_IP}:8443/wsg/api/public/${apiVersion}`;

const prettyJson = text => {
    console.log(JSON.stringify(JSON.parse(text), null, 2));
};

const showResponse = async res => {
    console.log(`${res.url} -> ${res.status}`);
    let text = await res.text();
    if (text === "") {
        text = "{}";
    }
    prettyJson(text);
};

const login = async (base_url) => {
    const opt = {
        method: "POST",
        body: JSON.stringify({
            "userName": userName,
            "password": password
        }),
    };
    const url = `${base_url}/session`;
    const res = await fetch(url, opt);
    await showResponse(res);

    COOKIE = await res.headers.get('set-cookie');
    return COOKIE;
};

const logout = async (base_url) => {
    const opt = {
        method: "DELETE",
        "headers": {
            "cookie": COOKIE
        }
    };
    const url = `${base_url}/session`;
    const res = await fetch(url, opt);
    await showResponse(res);

    COOKIE = "";
};

const getZone = async (base_url) => {
    const url = `${base_url}/rkszones`;
    const opt = {
        "method": "GET",
        "headers": {
            "cookie": COOKIE
        }
    };
    const res = await fetch(url, opt);
    await showResponse(res);
};


//
// main
//

(async (url) => {
    await login(`${url}`);
    await getZone(url);
    await logout(url)
})(base_url);

