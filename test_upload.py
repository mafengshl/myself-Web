import requests
import base64

# 先登录获取token
login_data = {
    'username': 'admin',
    'password': '123456'
}

login_response = requests.post('http://localhost:8080/api/auth/login', data=login_data)
print("登录响应:", login_response.text)

if login_response.status_code == 200:
    result = login_response.json()
    if result.get('code') == 200:
        token = result['data']['token']
        print("获取token成功:", token[:20] + "...")
        
        # 测试上传
        headers = {
            'Authorization': f'Bearer {token}'
        }
        
        # 创建一个测试图片（小的base64图片）
        test_image_data = 'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M/wHwADhgGAWjR9awAAAABJRU5ErkJggg=='
        test_image_bytes = base64.b64decode(test_image_data)
        
        files = {
            'file': ('test.png', test_image_bytes, 'image/png'),
            'description': (None, '测试照片')
        }
        
        upload_response = requests.post('http://localhost:8080/api/photos/upload', headers=headers, files=files)
        print("上传响应:", upload_response.text)
    else:
        print("登录失败:", result.get('message'))
else:
    print("登录请求失败:", login_response.status_code)
