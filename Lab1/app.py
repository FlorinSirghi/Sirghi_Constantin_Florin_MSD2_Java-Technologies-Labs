import requests

resp = requests.get(
    "http://localhost:8080/Lab1_war_exploded/api/echo",
    params={"value": "1"},
    headers={
        "User-Agent": "MyPythonClient/0.1",
        "Accept-Language": "en-US,en;q=0.9,ro;q=0.8",
    },
    timeout=5,
)
print(resp.status_code, resp.text)
