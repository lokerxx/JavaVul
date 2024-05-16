from flask import Flask, render_template,jsonify, request
import requests
from vul import requests_config
import os

app = Flask(__name__)


proxy_mode = False
proxies = {
    "http": "http://1.1.1.1:8080",
    "https": "https://1.1.1.1:8080",
}

@app.route('/get-request-config')
def get_request_config():
    api = request.args.get('api')
    config = requests_config.get(api, {})
    return jsonify(config)

@app.route('/replay-request', methods=['POST'])
def replay_request():
    try:
        data = request.json  # 前端发送的请求数据，包括 api、method、url、headers、data 等信息
        print(data)
        api = data['api']
        method = data['method']
        url = data['url']
        headers = data['headers']
        requestData = data['data']

        if method == 'GET':
            response = requests.get(url=url, headers=headers)
        elif method == 'POST':
            response = requests.post(url=url, headers=headers, data=requestData)

        response_data = {
            'status_code': response.status_code,
            'headers': dict(response.headers),
            'text': response.text
        }

        return jsonify(response_data)

    except Exception as e:
        response_data = {
            'status_code': 500,
            'headers': dict({}),
            'text': str(e)
        }
        return jsonify(response_data)

@app.route('/')
def index():
    attack_data = [{**config, 'api': key} for key, config in requests_config.items() if config['type'] == 'attack']
    normal_data = [{**config, 'api': key} for key, config in requests_config.items() if config['type'] == 'normal']
    repair_data = [{**config, 'api': key} for key, config in requests_config.items() if config['type'] == 'repair']
    mistake_data = [{**config, 'api': key} for key, config in requests_config.items() if config['type'] == 'mistake']

    # When rendering the template
    return render_template('index.html',
                           attack_data=attack_data,
                           normal_data=normal_data,
                           repair_data=repair_data,
                           mistake_data = mistake_data
                           )

@app.route('/<path:attack_type>', methods=['GET', 'POST'])
def handle_request(attack_type):
    if attack_type in requests_config:
        config = requests_config[attack_type]
        url = config['url']
        headers = config['headers']
        method = config['method']

        if 'file' in config:
            try:
                # 处理文件上传
                file_path = config['file']
                param_name = config['parm']
                if os.path.exists(file_path):
                    with open(file_path, 'rb') as f:
                        files = {param_name: (os.path.basename(file_path), f)}
                        if proxy_mode == True:
                            response = requests.post(url=url, headers=headers, files=files,proxies=proxies)
                        else:
                            response = requests.post(url=url, headers=headers, files=files)
                else:
                    return f"File {file_path} not found", 404
            except Exception as e :
                return str(e)
        else:
            # 处理非文件上传请求
            try:
                if method == 'GET':
                    if proxy_mode==True:
                        response = requests.get(url=url, headers=headers,proxies=proxies)
                    else:
                        response = requests.get(url=url, headers=headers)
                elif method == 'POST':
                    data = config.get('data', {})
                    if proxy_mode==True:

                        response = requests.post(url=url, headers=headers, data=data,proxies=proxies)
                    else:
                        response = requests.post(url=url, headers=headers, data=data)
            except Exception as e :
                return str(e)
        return response.text
    else:
        return "Invalid attack type", 400



if __name__ == '__main__':
    app.run(debug=True)
