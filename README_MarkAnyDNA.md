# Mark Any DNA Integration

## Install Java

```bash
sudo yum install java-17-amazon-corretto
```

### The installation location

```bash
/usr/lib/jvm/java-17-amazon-corretto
```

<br />
<br />

## Set the environment variable on `.bash_profile`

```bash 
if [ -f ~/.bashrc ]; then
        . ~/.bashrc
fi

export PATH=$PATH:/usr/lib/jvm/java-17-amazon-corretto/bin/
```

<br />
<br />

## The installation location of Mark Any DNA

```bash
/home/ec2-user/markanyDNA
```

<br />
<br />

## The location of temp files and log files

- /home/ec2-user/markanyDNA/FPS/files/tmp
- /home/ec2-user/markanyDNA/FPS/log

<br />
<br />

## The properties of Mark Any DNA

| property name                  | value                                                    |
|--------------------------------|----------------------------------------------------------|
| external.markany.dna.watermark | /home/ec2-user/markanyDNA/FPS/files/copy_detector.bmp    |
| external.markany.dna.port      | 9091                                                     |
| external.markany.dna.hosts     | The IPs of EC2s e.g. 127.0.0.1;33.200.15.254 (semicolon) |
| external.markany.dna.enabled   | true or false                                            |

<br />
<br />

## How to run the daemon of Mark Any DNA

```bash
~/2D/AdminStart_direct.sh
~/markanyDNA/controlDaemon.sh start
```

## How to check the status of the daemon of Mark Any DNA

```bash
~/2D/AdminStatus_direct.sh
~/markanyDNA/controlDaemon.sh status
```

## How to stop the daemon of Mark Any DNA

```bash
~/2D/AdminStop_direct.sh
~/markanyDNA/controlDaemon.sh stop
```

## The `user data` for EC2

```bash
#!/bin/bash

sh /home/ec2-user/2D/AdminStart_direct.sh
sh /home/ec2-user/markanyDNA/controlDaemon.sh start
```

## How to check if EC2 can access Mark Any DNA Server

Go to EC2 machine(Act-Rest-API)

### Install the Netcat

```bash
yum install -y nc
```

### Check if EC2 can access Mark Any DNA Server

```bash
nc -u -v ${MARK_ANY_DNA_SERVER_IP} 9091
```

## Update the license file on Mark Any DNA Server

Override the license file on the following location

```bash
~/2D/FPS/.MarkanyLicenseFps
```
