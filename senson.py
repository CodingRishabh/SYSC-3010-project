# -*- coding: utf-8 -*-
from time import sleep
import subprocess
import RPi.GPIO as GPIO
import sys
import socket
import spidev #import spidev and open SPI to work with ADC on Gertboard

#dependend on the Pin you use on Gertboard select 0 (for AD0) or 1 (for AD1)
channel = 0; 
host = '192.168.43.214' #server IP
textport = 6777 #port used 

#this method is used to read Analog Values
#channel: input: channel port number(knowing that there are 2 ADC input ports) 
def read(channel):
       #send start bit, sgl/diff, odd/sign, MSBF to SPI
        adc=spi.xfer2([1,(2+channel)<< 6,0])
        
        #spi.xfer2 returens same number of 8-bit bytes as sent
        #we parse out the part of bits which includes the changing value of the sensor
        adc_value=((adc[1]&31) << 6) + (adc[2] >> 1) 

        #print analgue value 
        print(adc_value)

        # delay after each print
        sleep(1)

        return adc_value
		
#this method is used to make LED flash 
#port: LED port number, 
#sec: number of seconds between each flush
def flash(port, sec):
        i=3
       
        while i>0:
                #if GPIO.input(26)
                        #print("acknowladged")
                GPIO.output(port, 1)
                sleep(sec)
                GPIO.output(port, 0)
                i=-1
        


#setting up socket 
s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
port = int(textport)
server_address = (host, port)



#initialise RPI.GPIO
GPIO.setmode(GPIO.BCM)  
GPIO.setwarnings(False)
GPIO.setup(25, GPIO.OUT)
GPIO.setup(26, GPIO.IN)

#testing led, and an indication of starting the sensor
#GPIO.output(25, 1)
#sleep(2)	
#GPIO.output(25, 0)

#creating spidev object
spi=spidev.SpiDev()

#the ADC of the Gertboard is on SPI channel 0
spi.open(0,0)

#connecting with server
pc='pc'
print("making connection")
s.sendto(pc.encode('utf-8'), server_address)
sleep(1)
print("waiting to receive on port %d : press Ctrl-CBreack to stop" % port)
buf, address = s.recvfrom(port)
print("connection completed")
sleep(1)
print(address)


while 1: 
        sensor_input = read(channel) #reading from sensor
        if sensor_input >100: 
                flash(25, 0.25)
        sensor_input = str(read(channel))
      
        #sending the data to the server
        s.sendto(sensor_input.encode('utf-8'), address)
s.shutdown(1)
         
	
	
