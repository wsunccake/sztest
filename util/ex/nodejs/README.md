# setup

```bash
[linux:nodejs] $ npm init
[linux:nodejs] $ npm install node-fetch
[linux:nodejs] $ vi package.json
...
  "type": "commonjs"
...
```


---

# run

```bash
[linux:nodejs] $ export API_VERSION=v9_0
[linux:nodejs] $ export SZ_IP=<ip>
[linux:nodejs] $ export SZ_USERNAME=admin
[linux:nodejs] $ export SZ_PASSWORD=<password>

[linux:nodejs] $ node sz_api.js
```
