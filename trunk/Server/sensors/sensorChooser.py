# sensorChooser.py
# provide an interface for getting a specific sensor object

# import all the sensors
from sensors.genericSensor import GenericSensor
from sensors.sampleListSensor import SampleListSensor
from sensors.sampleRangeSensor import SampleRangeSensor

# simple function that takes a string and returns a sensor object
def getSensorObject(s):
    if s == 'sampleList':
        return SampleListSensor()
    elif s == 'sampleRange':
        return SampleRangeSensor()
    else:
        return GenericSensor()
        