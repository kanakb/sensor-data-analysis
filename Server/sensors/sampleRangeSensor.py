# sampleRangeSensor.py
# serves as an example for how to specify details about a given sensor
# this one has a domain of a range of values

# need GenericSensor to subclass it
from sensors.genericSensor import GenericSensor
from sensors.genericSensor import DataType

# sensor class definition -- inherits from a GenericSensor
class SampleRangeSensor(GenericSensor):
    def __init__(self):
        # define all fields specific to this sensor
        self.dataType = DataType.FloatRange
        