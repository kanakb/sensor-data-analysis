# extractAggregate.py
# an easy way to pass sensor data without a client

from google.appengine.api import users
from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app

class AggregateDataExtracter(webapp.RequestHandler):
    def get(self):
        self.response.out.write("""
            <html>
                <body>
                    <form action="/heatgridGen" method="post">
                        Measurement Data: <br />
                        <textarea rows="40" cols="100" name="xml"></textarea><br />
                        Rows: <input type="text" name="xDim" /><br />
                        Columns: <input type="text" name="yDim" /><br />
                        <input type="submit" value="Submit" />
                    </form>
                </body>
            </html>""")

application = webapp.WSGIApplication(
                                     [('/extractAggregate', AggregateDataExtracter)],
                                     debug=True)

def main():
    run_wsgi_app(application)

if __name__ == "__main__":
    main()