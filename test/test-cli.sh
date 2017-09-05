#!/bin/bash
#
# Linux-based acceptance tests
#

(( failure_count = 0 ))

function assert_status {
if [[ "$?" == "${1}" ]]
then
   echo "TEST: ${2}: PASSED"
else
   echo "TEST: ${2}: FAILED with $?"
   (( failure_count = failure_count + 1 )) 
fi
}

#
# Prove that console can get password - must be run manually
#
#java -cp dist/*:needed/*:keystore org.birch.cryptomainia.EncryptArgv
#assert_status 0 "console can get password"

#
# Prove that a bad password results in non-zero exit status. 
#
java -cp dist/*:needed/*:keystore org.birch.cryptomainia.DecryptArgv org.birch.cryptomainia.DummyTarget 2 A 'bad_encrypted_password' C D E F G
assert_status 1 "bad password results in non-zero exit status."

#
# Prove that password is decrypted and passed to next main(). 
#
set -o pipefail
java -cp dist/*:needed/*:keystore org.birch.cryptomainia.DecryptArgv org.birch.cryptomainia.DummyTarget 2 A 'Kfx3hrZ5p/pf1UkVeNAxOg==' C D E F G | grep Guava
assert_status 0 "password is decrypted and passed to next main()"

#
# Test that un-encrypyted password is not available in process listing
#
java -cp dist/*:needed/*:keystore org.birch.cryptomainia.DecryptArgv org.birch.cryptomainia.SleepingTarget 1 'Kfx3hrZ5p/pf1UkVeNAxOg==' &
sleep 5
ps -elf | grep cryptomainia | grep Guava
assert_status 1 "un-encrypyted password is not available in process listing."

#
# Test bad arguments detected in Sub-process execution
#
java -cp dist/*:needed/*:keystore org.birch.cryptomainia.DecryptArgv
assert_status 1 "bad arguments detected"
java -cp dist/*:needed/*:keystore org.birch.cryptomainia.DecryptSubprocess
assert_status 1 "bad arguments detected"

#
# Test bad password detected in Sub-process execution
#
java -cp dist/*:needed/*:keystore org.birch.cryptomainia.DecryptSubprocess TESTPASSWORD 'zKfx3hrZ5p/pf1UkVeNAxOg==' /bin/bash -c 'echo $TESTPASSWORD'
assert_status 1 "bad password in sub-process execution."

#
# Test bad environment variable detected in Sub-process execution
#
java -cp dist/*:needed/*:keystore org.birch.cryptomainia.DecryptSubprocess HOME 'zKfx3hrZ5p/pf1UkVeNAxOg==' /bin/true
assert_status 1 "bad environment variable in sub-process execution."

#
# Test decrypted password passed to sub-process as environment variable
#
java -cp dist/*:needed/*:keystore org.birch.cryptomainia.DecryptSubprocess TESTPASSWORD 'Kfx3hrZ5p/pf1UkVeNAxOg==' /bin/bash -c 'echo $TESTPASSWORD' | grep Guava
assert_status 0 "decrypted password passed to sub-process as environment variable"

#
# Test environment is passed through intact
#
number_of_variables=$(env | wc -l)
new_number_of_variables=$(java -cp dist/*:needed/*:keystore org.birch.cryptomainia.DecryptSubprocess TESTPASSWORD 'Kfx3hrZ5p/pf1UkVeNAxOg==' /usr/bin/env | wc -l)
if (( new_number_of_variables > number_of_variables ))
then
    echo "TEST: environment is passed through intact: PASSED"
else
    echo "TEST: environment is passed through intact: FAILED: count was ${new_number_of_variables}, should be 1 + ${number_of_variables}"
   (( failure_count = failure_count + 1 )) 
fi

#
# Test environment is passed through intact 2
#
number_missing_variables=$(comm -23 <( env | grep -v '_=' | sort ) <(java -cp dist/*:needed/*:keystore org.birch.cryptomainia.DecryptSubprocess TESTPASSWORD 'Kfx3hrZ5p/pf1UkVeNAxOg==' /usr/bin/env | sort ) | wc -l)

if (( number_missing_variables > 0 ))
then
    echo "TEST: environment is passed through intact: FAILED: # missing variables was ${number_missing_variables}"
   (( failure_count = failure_count + 1 )) 
else
    echo "TEST: environment is passed through intact: PASSED"
fi

#
# Test Python itengration
#
python_raw=$(readlink -f $(which python))
echo "Using ${python_raw}"
java -cp dist/*:needed/*:keystore org.birch.cryptomainia.DecryptSubprocess TESTPASSWORD 'Kfx3hrZ5p/pf1UkVeNAxOg==' ${python_raw} -c "import os; print os.environ['TESTPASSWORD']" | grep Guava
assert_status 0 "decrypted password passed to Python as environment variable"

echo '------------------------------------------------------------------------------'
if [[ "$failure_count" != "0" ]]
then
   echo "TESTS FAILED: ${failure_count}"
   exit 1
else
   echo "ALL TESTS PASSED"
fi
