# genericSensor.py
# define a base class to dictate how sensor data should be handled

# Application Imports
from lib import senselib

# define an emum describing the domain of the sensor data
DataType = senselib.enum('List', 'Range')

class GenericSensor:
    # set key sensor properties
    def __init__(self):
        self.dataType = None
        
    # accessor for data type
    def getDataType(self):
        return self.dataType
        