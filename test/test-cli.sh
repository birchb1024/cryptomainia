#!/bin/bash
#
# Linux-based acceptance tests
#

#
# Prove that console can get password
#
java -cp dist/*:needed/*:keystore org.birch.cryptomainia.EncryptArgv


#
# Prove that password is decrypted and passed to next main(). 
#
java -cp dist/*:needed/*:keystore org.birch.cryptomainia.DecryptArgv org.birch.cryptomainia.DummyTarget 2 A 'Kfx3hrZ5p/pf1UkVeNAxOg==' C D E F G | grep Guava
if [ $? != 0 ]
then
   echo "FAILED"
else 
   echo "PASSED"
fi

#
# Test that un-encrypyted password is not available in process listing
#
java -cp dist/*:needed/*:keystore org.birch.cryptomainia.DecryptArgv org.birch.cryptomainia.SleepingTarget 1 'Kfx3hrZ5p/pf1UkVeNAxOg==' &
sleep 5
ps -elf | grep cryptomainia | grep Guava
if [ $? == 0 ]
then
   echo "FAILED"
else 
   echo "PASSED"
fi
