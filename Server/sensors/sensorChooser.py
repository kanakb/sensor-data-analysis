# sensorChooser.py
# provide an interface for getting a specific sensor object

# import all the sensors
from sensors import GenericSensor

# simple function that takes a string and returns a sensor object
def getSensorObject(s):
    if (s == 'generic'):
        return GenericSensor()
    else:
        return GenericSensor()
        