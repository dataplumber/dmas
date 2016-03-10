set tmp =  `ps -ef | grep IWS | grep -v "grep" | awk '{print $2}'`
echo "killing $tmp"
kill $tmp
