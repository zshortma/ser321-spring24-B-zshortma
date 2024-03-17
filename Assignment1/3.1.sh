{\rtf1\ansi\ansicpg1252\cocoartf2757
\cocoatextscaling0\cocoaplatform0{\fonttbl\f0\fswiss\fcharset0 Helvetica;}
{\colortbl;\red255\green255\blue255;}
{\*\expandedcolortbl;;}
\margl1440\margr1440\vieww11520\viewh8400\viewkind0
\pard\tx720\tx1440\tx2160\tx2880\tx3600\tx4320\tx5040\tx5760\tx6480\tx7200\tx7920\tx8640\pardirnatural\partightenfactor0

\f0\fs24 \cf0 #!/bin/bash\
\
\
get_current_time() \{\
    date +"%T"\
\}\
\
\
get_socket_counts() \{\
    netstat -an | grep -E '^(tcp|udp)' | awk '\{print $6\}' | sort | uniq -c\
\}\
\
\
echo "Time     LISTENING  ESTABLISHED"\
end_time=$((SECONDS + 600))  # 600 seconds = 10 minutes\
\
while [ $SECONDS -lt $end_time ]; do\
    echo "$(get_current_time) $(get_socket_counts)"\
    sleep 30\
done\
}