# Introduction #

Here's some notes from things we came up with when we were talking about research on 4/5/11.

# Notes #

  * Try to leverage Yahoo! Pipes
    * RSS feeds
    * CSV files
    * Possibly others
  * Look for a standard format to make it easier to use frameworks that can help us analyze the data
  * Google Maps for Flash

# Interfaces #
  * Getting raw data vs. getting aggregate data
  * XML format
  * Input latitude/longitude, time of measurement, sensor type
    * Could be a square region
  * Add device type, device id (perhaps hashed for anonimity)
  * Big XML tag is "measurement" for measurement
    * device type
    * device id
    * latitude
    * longitude
    * time of measurement
    * sensor type (string)
    * sensor data (binary)
  * Make it easy to add new sensors (should start very generic)
  * Server to client (support raw and simple aggregate data)
    * option: pass generic query (sanitize input)
    * comma list of device type, id
    * range for lat/long
    * range of measurements
    * CSV of sensor type
    * treatment of sensor data based on sensor type (??)
  * Try to implement something like a heat map