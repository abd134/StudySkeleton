import http.server
import http.cookies
import time
import ssl
from io import BytesIO
import random
import string
import base64
import json

hostName = "localhost"
serverPort = 8080

def transaction(sender,receiver,amount):
    #pseudo function to mimic transaction
    return

class MyServer(http.server.BaseHTTPRequestHandler):
    def do_GET(self):
        self.send_response(200)
        self.send_header("Content-type", "text/html")
        self.end_headers()
        self.wfile.write(bytes("<html><head><title>Payment Server</title></head>", "utf-8"))
        self.wfile.write(bytes("<p>Request: %s</p>" % self.path, "utf-8"))
        self.wfile.write(bytes("<body>", "utf-8"))
        self.wfile.write(bytes("<p>SUCCESS.</p>", "utf-8"))
        self.wfile.write(bytes("</body></html>", "utf-8"))
    def do_POST(self):
        content_length =  int(self.headers['Content-Length'])
        # body = self.rfile.read(content_length).decode("utf-8")
        message = json.loads(self.rfile.read(content_length))
        print(self.headers)
        if message["Request"] == "Login":
            # print(message['Username'])
            # print(message['Password'])
            # print(body)
            if message["Username"] ==  "jackielee" and message["Password"] == "brucechan":
                self.send_response(200)
                cookie=http.cookies.SimpleCookie()
                cookie['user'] = message["Username"]
                for morsel in cookie.values():
                    self.send_header("Set-Cookie",morsel.OutputString())
                self.end_headers()
                response = BytesIO()
                response.write(b'LOGGED IN')
                self.wfile.write(response.getvalue())
            else:
                self.send_response(200)
                self.end_headers()
                response = BytesIO()
                response.write(b'FAILED')
                self.wfile.write(response.getvalue())
        elif message["Request"] == "Payment":
                self.send_response(200)
                cookie=http.cookies.SimpleCookie(self.headers.get("Cookie"))
                if cookie['user'] == "NA":
                    self.end_headers
                    response = BytesIO()
                    response.write(b'SIGN IN AGAIN')
                    self.wfile.write(response,getvalue())
                else:
                    for morsel in cookie.values():
                        self.send_header("Set-Cookie",morsel.OutputString())
                    self.end_headers()
                    transaction(cookie['user'],message["Recipient"],message["Amount"])
                    response = BytesIO()
                    response.write(b'SENT ')
                    response.write(str(message["Amount"]).encode("utf-8"))
                    response.write(b" TO ")
                    response.write(str(message["Recipient"]).encode("utf-8"))
                    self.wfile.write(response.getvalue())
        else:
            self.send_response(400)
            self.end_headers()


if __name__ == "__main__":        
    webServer = http.server.HTTPServer((hostName, serverPort), MyServer)
    webServer.socket = ssl.wrap_socket(webServer.socket, server_side=True,certfile='servercert.pem',ssl_version=ssl.PROTOCOL_TLS)
    print("Server started http://%s:%s" % (hostName, serverPort))

    try:
        webServer.serve_forever()
    except KeyboardInterrupt:
        pass

    webServer.server_close()    
    print("Server stopped.")