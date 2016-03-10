import cx_Oracle
import ConfigParser
import sys, getopt 

def getQueries(input, queries):
   if input == 'all':
       return queries.keys() 
   else:
       lst = []
       for x in input.split(','):
          if x in queries.keys():
              lst.append(x)
       return lst 

def main(argv):


    try:
        opts, args = getopt.getopt(argv,"hc:q:")
    except getopt.GetoptError as err:
        print str(err)
        print sys.argv[0] + '-c <config> -q <queries>'
        print 'queries can be \'all\' or a list like \'dataset,collection\'' 
        sys.exit(2)

    configFile = None
    qs = None

    for opt, arg in opts:
        if opt == '-h':
           print sys.argv[0] + '-c <config> -q <queries>'
           print 'queries can be \'all\' or a list like \'dataset,collection\'' 
           sys.exit()
        elif opt in ("-c", "--ifile"):
           configFile = arg
        elif opt in ("-q", "--ofile"):
           qs = arg

    if qs is None:
        qs = 'all' 

    if configFile is None:
        print "ConfigFile must be specified with the -c option"
        sys.exit(3)

    
    config = ConfigParser.ConfigParser()
    config.read(configFile)

    ip = config.get('connection', 'host')
    port = int(config.get('connection', 'port'))
    SID = config.get('connection', 'SID') 

    user = config.get('connection', 'user') 
    password = config.get('connection', 'pass')


    queries = {}

    for section in config.sections():
        if section == 'connection':
            continue
        #print "section: " + section
        s = set() 
        for opt in config.options(section):
            #print opt + ":" + config.get(section,opt) 
            s.add(opt.split('.')[0])

        queries[section] = s


    #print queries.keys()
    #for x in queries['collection']:
    #    print x


    dsn_tns = cx_Oracle.makedsn(ip, port, SID)
    con = cx_Oracle.connect(user,password, dsn_tns)
    cur = con.cursor()

    lst = getQueries(qs, queries)
    for x in lst:
        print "\n\n*************  " + x + " queries  ***************\n"

        for opt in queries[x]:
            q = config.get(x,opt+'.query')
            t = config.get(x,opt+'.title', None )

            print "------------------------------------------"
            print t
            print "------------------------------------------"
            cur.execute(q)
            for result in cur:
                print result[0]   


    cur.execute('select short_name from dataset order by short_name')

    #for result in cur:
    #    print result

    cur.close()
    con.close()         
    
if __name__ == "__main__":
   main(sys.argv[1:])
