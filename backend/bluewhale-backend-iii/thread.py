import http.client
import json
import re
import threading

req_count = 0
lock = threading.Lock()
payload = json.dumps({
    "productId": 2,
    "storeId": 1,
    "num": 1,
    "userId": 4,
    "deliveryMethod": "PICKUP",
    "orderState": "UNPAID"
})
headers = {
    'token': 'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI3IiwiZXhwIjoxNzEzMDgxMTgxfQ.5f68XRN4-UovSnKHzsWZF9_Rzz8M2LU7B_gQBaDwMOw',
    'User-Agent': 'Apifox/1.0.0 (https://apifox.com)',
    'Content-Type': 'application/json',
    'Accept': '*/*',
    'Host': '127.0.0.1:8080',
    'Connection': 'keep-alive'
}


def extract_code(json_string):
    # 定义正则表达式模式
    pattern = r'\"code\":\"(\d{3})\"'

    # 使用正则表达式进行匹配
    match = re.search(pattern, json_string)

    if match:
        # 如果匹配成功，返回提取到的nnn部分
        return match.group(1)
    else:
        # 如果没有匹配到，返回None或者其他适当的值
        return None


def one_req(conn):
    global req_count
    conn.request("POST", "/api/orders", payload, headers)
    res = conn.getresponse()
    data = res.read()
    text = data.decode("utf-8")
    if extract_code(text) == "000":
        with lock:
            req_count += 1

# worker 100
def worker(thread_id):
    conn = http.client.HTTPConnection("127.0.0.1", 8080)
    print(f"Thread {thread_id} started")
    for i in range(100):
        one_req(conn)
    print(f"Thread {thread_id} finished")


if __name__ == '__main__':
    threads = []
    for i in range(100):
        thread = threading.Thread(target=worker, args=(i,))
        thread.start()
        threads.append(thread)
    
    for thread in threads:
        thread.join()
    
    print("all sent req: " + str(10000))
    print("all succ req: " + str(req_count))
