# -*- coding: utf-8 -*-
import RPi.GPIO as GPIO
import time
import serial


def main():
    ser=serial.Serial("/dev/ttyAMA0",9600,timeout=0.1)
    while True:
        tmp=ser.read(30)
        if tmp:
            print "beep"
            GPIO.setmode(GPIO.BOARD)
            GPIO.setup(31,GPIO.OUT)
            GPIO.output(31,GPIO.HIGH)
            time.sleep(0.3)
            GPIO.cleanup()

main()
GPIO.Cleanup()
