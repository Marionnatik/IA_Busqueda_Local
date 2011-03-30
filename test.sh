#!/bin/bash
          if [ -z "$1" ]; then 
              echo "No m'has escrit quin test vols fer!"
              exit
	  fi

if [ "$1" -eq 1 ]; then
	rm tests/test1/*output*
	java -jar provaex.jar tests/test1/test1.txt
elif [ "$1" -eq 2 ]; then
	rm tests/test2/*output*
	for mia_var in tests/test2/test2?.txt; do
		java -jar provaex.jar "$mia_var"
	done
elif [ "$1" -eq 3 ]; then
	rm tests/test3/*output*
elif [ "$1" -eq 4 ]; then
	rm tests/test4/*output*
elif [ "$1" -eq 5 ]; then
	rm tests/test5/*output*
elif [ "$1" -eq 6 ]; then
	rm tests/test6/*output*
elif [ "$1" -eq 7 ]; then
	rm tests/test7/*output*
elif [ "$1" -eq 8 ]; then
	rm tests/test8/*output*
else
echo "Que vols?! Aquel test no existeix..."
fi


