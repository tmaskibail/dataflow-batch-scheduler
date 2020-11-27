# Dataflow Batch Scheduler 

This is a dummy application which 
* triggers once every x minutes
* moves all the CSV files from a source GCS bucket to a processing GCS bucket 
* triggers Dataflow batch job to ingest the data from CSV files in processing GCS bucket into a Bigquery table 
* polls the Dataflow job via REST API to check whether it was DONE/CANCELLED/FAILED 
* moves all the CSV files from processing GCS bucket to an archive GCS bucket 
* repeat 

This is a demo application and hence doesn't handle a lot of edge use cases.
It tires to simulate steps which could have been implemented using Cloud Composer/Airflow
The idea is to deploy this continuously running application in a Google App Engine and orchestrate the steps using REST API calls to GCP products.

Note that users are expected to provide all the necessary details in the application.properties file.

More information on the Dataflow Templates can be found here :  https://cloud.google.com/dataflow/docs/guides/templates/provided-batch#gcstexttobigquery
 
