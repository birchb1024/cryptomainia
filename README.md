cryptomainia
============

Utility for encrypting password arguments passed to Java command-line applications.

Some essential java applications take passwords as command-line arguments. 
(e.g -Dhttp.proxyPassword=somePassword) This is unfortunate because command-line arguments are visible
in log files and in process tables and hence the passwords are easily viewable
by anyone with access to the machine. 
 
The goal of this utility is hide passwords from the operating system by externally
encrypting them then having them decrypted within the Java process before they
 are passed to the application. 
 
The utility should be general enough for masking passwords for any Java 
application, not just Liquibase.
 
HOW IT WORKS
------------

The main class of the target program is usurped by a another main class provided 
by the utility. (org.birch.cryptomainia.DecryptArgv). DecryptArgv.main() reads 
a property file which is in the classpath "cryptomainia.properties" which contains 
a key. It uses the first two command-line arguments to identify the target class 
whose main() is to be called and the encrypted argument which is decrypted. In 
pseudo code this is:

    User invokes DecryptArgv.main(target class name, id of encrypted arg, args to pass...)
    DecryptArgv.main:
      reads key file 
      decrypts args[id]
      load target class
      invoke target class.main(decrypted argument and the other args to pass...)

INSTALLATION
------------

There are two jar files needed: cryptomainia.jar and jasypt-1.9.0.jar. Download these and 
place them in a directory. These can be downloaded from here:

   https://sourceforge.net/projects/cryptomainia/files/?source=navbar

and here

   https://sourceforge.net/projects/jasypt/files/

USAGE
------------

There are three steps to using the utility:

1. Configure a key file
2. Encrypt passwords
3. Integrate with target application

### Configuration

A file called 'cryptomainia.properties' is expected in the classpath. The file 
is a standard Java properties file with two values as follows:

    algorithm: org.jasypt.util.text.BasicTextEncryptor  
    key: yourSecretKey  

Edit the file and save it somewhere that only authorised users have access. 
Choose an new key. 

### Encrypting passwords

The EncryptArgv class encrypts the argument and prints
the cipher-text on the standard output. As follows:

Given the following sub-directory structure:
 
     .
        dist
           cryptomainia.jar
           jasypt-1.9.0.jar
        keystore
           cryptomainia.properties

The command is as follows:

    $ java -cp dist/*:keystore org.birch.cryptomainia.EncryptArgv Password123 
    
    aPYPaxnrbKDUbaepoj4LSQWdoXhaD2P6

The encrypted password can then be saved to disk, typically in a configuration file used by a calling script.

### Integrating with the application

The DecryptArgv class uses the arguments passed to its main() function to manage the
target application.  DecryptArgv.main() expects arguments as follows:

1. name of the target application's main class . e.g. liquibase.integration.commandline.Main
2. either number or a string. 
     * If a number this is position of the password argument to be decrypted
     * If a string, it's a prefix which identifies a password parameter
3.  All other arguments are passed to the target class.main() 
 
 Examples:
 
#### Simple positional argument:
 
 Given the same  directory structure and liquibase as target, the command is:
 
     $ java -cp dist/*:keystore org.birch.cryptomainia.DecryptArgv \
        liquibase.integration.commandline.Main 1 \
        '5xXi1rFwqO5HWoIFJJ7walN9iWE4oIDCBGkmJtbQhKw=' C D E F G
 
#### With a string prefix. 

Some applications require a password to be passed prefixed with a switch or flag, for example `-Dpassword=XXXXX`. DecryptArgv will look for the first such argument and replace the password component with the decrypted string. For example, with the same directory structure as before:

    $ java -cp dist/*:keystore org.birch.cryptomainia.DecryptArgv \
         liquibase.integration.commandline.Main --password= --user=fred \
         '--password=5xXi1rFwqO5HWoIFJJ7walN9iWE4oIDCBGkmJtbQhKw=' A B C D ...
   
TESTING
------------

You can use the DummyTarget class to check the decryption is as expected. The class's main() prints its arguments. For example replace the target application with the dummy as follows:

    $ java -cp dist/*:keystore org.birch.cryptomainia.DecryptArgv \
    >     org.birch.cryptomainia.DummyTarget --password= --user=fred \
    >     '--password=5xXi1rFwqO5HWoIFJJ7walN9iWE4oIDCBGkmJtbQhKw=' A B C D         ...
    DummyTarget: --user=fred --password=MYENCRYPTEDPASSWORD A B C D ... 

ADVANCED
------------

To decrypted more than one password you can chain recursive invocations
of DecryptArgv. The following example decrypts two passwords using positional parameters.
 
    $ java -cp dist/*:keystore org.birch.cryptomainia.DecryptArgv \
    >      org.birch.cryptomainia.DecryptArgv 3 \
    >      org.birch.cryptomainia.DummyTarget 2 \
    >      '5xXi1rFwqO5HWoIFJJ7walN9iWE4oIDCBGkmJtbQhKw=' \
    >      'ZGXmpDNKPzj54hyZWi+9Kw==' 
    DummyTarget: MYENCRYPTEDPASSWORD BBBB

