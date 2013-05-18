#!/bin/bash
#
# Linux-based acceptance tests
#

#
# Prove that password is decrypted and passed to next main(). 
#
java -jar dist/cryptomainia.jar org.birch.cryptomainia.DummyTarget 2 A '5xXi1rFwqO5HWoIFJJ7walN9iWE4oIDCBGkmJtbQhKw=' C D E F G | grep MYENCRYPTEDPASSWORD
if [ $? != 0 ]
then
   echo "FAILED"
else 
   echo "PASSED"
fi

#
# Test that un-encrypyted password is not available in process losting
#
java -jar dist/cryptomainia.jar org.birch.cryptomainia.SleepingTarget 1 '5xXi1rFwqO5HWoIFJJ7walN9iWE4oIDCBGkmJtbQhKw=' &

ps -elf | grep cryptomainia | grep MYENCRYPTEDPASSWORD
if [ $? == 0 ]
then
   echo "FAILED"
else 
   echo "PASSED"
fi
