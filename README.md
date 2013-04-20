openshift-mongo-java-spatial
=============================

This is the code to go along with the [OpenShift blog piece](???????) on how to use Flask (python) with MongoDB to create a REST like web service with spatial data

Running on OpenShift
----------------------------

Create an account at http://openshift.redhat.com/

Create a python-2.6 application and add a MongoDB cartridge to the app

    rhc app create javaws jbosseap-6
    rhc cartridge mongodb-2.2 --app javaws

Add this upstream flask repo


    cd javaws
    git remote add upstream -m master https://github.com/thesteve0/openshift-mongo-spatial-jee6.git
    git pull -s recursive -X theirs upstream master
    
Then push the repo upstream

    git push
    
To add the data to the MongoDB instance please follow the instructions on this blog:
[Mongo Spatial on OpenShift](https://openshift.redhat.com/community/blogs/spatial-mongodb-in-openshift-be-the-next-foursquare-part-1)

Now, ssh into the application.

Add the data to a collection called parkpoints:

    mongoimport -d javaws -c parkpoints --type json --file $OPENSHIFT_REPO_DIR/parkcoord.json  -h $OPENSHIFT_MONGODB_DB_HOST  -u admin -p $OPENSHIFT_MONGODB_DB_PASSWORD

    
Create the spatial index on the documents:

    mongo
    use javaws
    db.parkpoints.ensureIndex( { pos : "2d" } );

Once the data is imported you can now checkout your application at:

    http://javaws-$yournamespace.rhcloud.com/ws/parks
    

