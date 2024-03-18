#!/bin/bash


get_current_time() {
    date +"%T"
}


get_socket_counts() {
    netstat -an | grep -E '^(tcp|udp)' | awk '{print $6}' | sort | uniq -c
}


echo "Time     LISTENING  ESTABLISHED"
end_time=$((SECONDS + 600))  # 600 seconds = 10 minutes

while [ $SECONDS -lt $end_time ]; do
    echo "$(get_current_time) $(get_socket_counts)"
    sleep 30
done
