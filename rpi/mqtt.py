import paho.mqtt.client as mqtt

import json
import urllib2
from time import sleep

topic = "json_flow"		#topic where we will recieve json graph from node red server

def mqttconnect():
	while True:
		try:
			sleep(3)
			print("Connecting to MQTT server ...")
			client.connect("ec2-52-40-46-54.us-west-2.compute.amazonaws.com", 1883, 60)
			break
		except:
			pass

# The callback for when the client receives a CONNACK response from the server.
def on_connect(client, userdata, rc):
    print("Connected with result code "+str(rc))
    client.subscribe(topic, 1)

def on_disconnect(client, userdata, rc):
	mqttconnect()

# The callback for when a PUBLISH message is received from the server.
def on_message(client, userdata, msg):
    print msg.payload
    try:
    	if msg.topic == topic:
        	json_data = msg.payload
        	#MAKE A POST REQUEST
        	req = urllib2.Request('http://localhost:1880/flows')
        	req.add_header('Content-Type', 'application/json')
        	response = urllib2.urlopen(req, json_data)
        	print 'Success'
    except Exception, e:
    	print str(e)     	

client = mqtt.Client(client_id="client1",clean_session=False,userdata=None)
client.on_connect = on_connect
client.on_message = on_message
client.on_disconnect = on_disconnect

mqttconnect()
client.loop_forever()
