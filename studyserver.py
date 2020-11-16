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
#Declare any global variables here.

def initglobalstate():
	#If you need to store any variables declare them as global here and set there initial state if any.	
	global confirmationEnabled
	confirmationEnabled = False  
	return
def initializeRegistration(user):
	#Implement initial registration here. 
	#User is the user for which initialization is taking place
	#Set All initilization variables here i.e attestation challenge.
	#All responses that you need to send back to the client must be returned as one string.
	return ""
def verifyCertificateChain(user,certificateChain):
	#Verify the certificate chain received here.
	#User is the user to whom the certificate chain belongs.
	#Return true if verification of certificate chain is successful.
	#Verification includes checking the key properties corresponding to the certificate chain.
	#For this study in order for verification to be successful you have to check that the key has the property "trusted confirmation required".
	#Also return all key properties(in a tuple or dictionary or list whichever you prefer).
	#Final return type has to be a 2-tuple where first element is a boolean indicating if verification was successful and
	#the second element must be the keyProperties.
	return (False, None)
def initializeTransaction(sender,receiver,amount):
	#Create a prompt that the sender must confirm.
	#Prompt must contain at least the receiver and the amount, the rest of the phrasing is upto you.
	#Also create any extra data you need to send i.e nonce
	#Return prompt and any extra data in one single string to be sent to the client
	return ""
def verifyConfirmationMessage(user,confirmationMessage):
	#ConfirmationMessage is the string containing both the confirmedData and the signature
	#Verify the signature on the confirmedData and return true if successful
	#Also ensure that the confirmed data includes the nonce corresponds to the payment being sent
	#User is the user for to which confirmed the message.
	return False

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
		#Add declaration for global variables here as well
		global confirmationEnabled
		content_length =  int(self.headers['Content-Length'])
		message = json.loads(self.rfile.read(content_length).decode("latin_1"))
		if message["Request"] == "Login":
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
				response = BytesIO()
				if confirmationEnabled == True:
					promptDetails = initializeTransaction(str(cookie['user']),message["Recipient"],message["Amount"])
					response.write(promptDetails.encode("utf-8"))
				else:
					response.write(b'SENT ')
					response.write(str(message["Amount"]).encode("utf-8"))
					response.write(b" TO ")
					response.write(str(message["Recipient"]).encode("utf-8"))
				self.wfile.write(response.getvalue())
		elif message["Request"] == "RegisterConfirmation":
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
				response = BytesIO()
				initVars=initializeRegistration(str(cookie['user']))
				response.write(initVars.encode("utf-8"))
				self.wfile.write(response.getvalue())
		elif message["Request"] == "RegisterCertificates":
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
				response = BytesIO()
				confirmationRegistrationSuccess,keyProperties = verifyCertificateChain(str(cookie['user']), message["CertificateChain"])
				print(keyProperties)
				confirmationEnabled = confirmationRegistrationSuccess
				response.write(str(confirmationRegistrationSuccess).encode("utf-8"))
				self.wfile.write(response.getvalue())
		elif message["Request"] == "ConfirmationRequest":
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
				response = BytesIO()
				confirmationMessageSuccess = verifyConfirmationMessage(str(cookie['user']), message["ConfirmationMessage"])
				response.write(str(confirmationMessageSuccess).encode("utf-8"))
				self.wfile.write(response.getvalue())
		else:
			self.send_response(400)
			self.end_headers()


if __name__ == "__main__":
	initglobalstate()
	webServer = http.server.HTTPServer((hostName, serverPort), MyServer)
	webServer.socket = ssl.wrap_socket(webServer.socket, server_side=True,certfile='servercert.pem',ssl_version=ssl.PROTOCOL_TLS)
	print("Server started http://%s:%s" % (hostName, serverPort))

	try:
		webServer.serve_forever()
	except KeyboardInterrupt:
		pass

	webServer.server_close()    
	print("Server stopped.")