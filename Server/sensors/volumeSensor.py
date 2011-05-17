# volumeSensor.py
# Expects a range of volume data

# need GenericSensor to subclass it
from sensors.genericSensor import GenericSensor
from sensors.genericSensor import DataType

# sensor class definition -- inherits from a GenericSensor
class VolumeSensor(GenericSensor):
    def __init__(self):
        # define all fields specific to this sensor
        self.dataType = DataType.FloatRange
        