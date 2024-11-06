import requests
import json

def compare_versions(version1, version2):
    v1_parts = [int(part) for part in version1.split('.')]
    v2_parts = [int(part) for part in version2.split('.')]
    for v1, v2 in zip(v1_parts, v2_parts):
        if v1 < v2:
            return -1
        elif v1 > v2:
            return 1
    return 0

def get_update():
    try:
        url = "https://api.github.com/repos/ElJoker63/Cambio-actual/releases/latest"
        resp = requests.get(url).text
        data = json.loads(resp)
        name = data['name'].replace(data['tag_name'], '')
        return name, data['body'], data['tag_name'], data['assets'][0]['browser_download_url'], data['assets'][0]['download_count']
    except Exception as e:
        return "Error", str(e), "v0.0.0", "#", "0"