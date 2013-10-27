CS380D_ProjI:Akash Mankar and Tisha
UTEID:anm2829 and tf5995
==================================

Slip days used (this project): 1 Slip days used (total): 1
==============================================================

Three Phase Commit:
====================
How to run this? Following example describes the scenario for 4 processes.
1 coordinator and 3 Participants

Requirements:
=============
For this, the requirements are:
1) 5 process execution windows.
4 windows for running 3 Participant processes and 1 for Coordinator process
and 1 more for a client process which sends the new transaction.
2) config file, config.properties  which contains information about how many processes
to simulate,host, port number, how to use different options like, 
deathAfter, precommitCount, commitCount, delay etc. By default these options are disabled.

Running Three Phase commit
==================================
Type below commands

To run a Coordinator:
java -jar ThreePhaseCommit.jar Coordinator:config1.properties
 
To run Participant:1 
java -jar ThreePhaseCommit.jar Participant:1:config1.properties

To run Participant:2 
java -jar ThreePhaseCommit.jar Participant:2:config1.properties

To run Participant:3 
java -jar ThreePhaseCommit.jar Participant:3:config1.properties

To start a Client process for sending a transaction:
java -jar ThreePhaseCommit.jar config1.properties
   |
   |---- To Send a transaction, use the following format
	 	To add a song
		   client;add;<songname>;<url>
	 	To delete a song
		   client;del;<songname>;<url>
	 	To edit a song
		   client;edit;<old_song_name>,<new_song_name>;<new_song_url>


Operations supported and Configurations
=========================================
In the config.properties file, You can adjust:
preCommitCount=n , default value (-1) --->> This is partial precommit. i.e. coordinator dies after sending  n PreCommits.

CommitCount=n, default value (-1) -->> This is a partial Commit, i.e. coordinator dies after sending n Commits.

deathAfter=i,j,k, default value (-1,-1,-1) -->> i = processToKill , j=SourceProcessID, k=number_of_Messages

delay=n, defaultvalue(0) --> n=10000 for 10 seconds of delay


Voting Mechanism Used
=====================
To Add: If the <Song,URL> pair does not exist in the playlist then it voted YES. 
	Else, it votes NO.

To Edit: If the <Old_Song> exists in the playlist it votes YES.
	 Else, it votes NO.

To del: If the <Song, URL> exists in the playlist it votes YES.
	 Else, it votes NO.


Output
======
created in the ThreePhaseCommit/log/ folder.
Stable Storage: DTLog<pid>.log e.g. DTLog0.log
Volatile Storage: The playlist is consisdered to be in volatile memory but we are creating Playlist<pid>.properties e.g.Playlist0.properties to verify the Database 
Consistency. In failure cases, the entire playlist is reconstructed from the DTLogs and not these files.
Debug logs(if required): all Logger logs named as Log<pid>.log e.g Log0.log

New Execution:
===============
To start a new Execution, clear DT logs written under
/ThreePhaseCommit/log/
Otherwise, each time on a process kill, and restart, it will treat it as recovering from failure, it will create the entire previous
playlist again and then execute again.

The Mistake in the textbook and the Reading
===========================================
Both say that we don't need to log the precommit.
But for a scenario mentioned below, this causes a problem because it violates the Atomic Commit AC rules.
Scenario (Without logging precommit):
   |--- Coordinator writes Commit to its logs after getting ACKs from everyone, but dies before sending commits to anyone.
   |--- Now the Coordinator dies. All the participants on timeout, run the election protocol whereby electing the operational process with the lowest PID as the new
        coordinator. The state of all participants is "COMMITABLE". 
   |--- At this point, all the participants die, thereby inducing total failure. 
   |--- Now all the participants start recovering except the coordinator. 
   |--- Since no precommit was logged, they see their votes and move to the state "UNCERTAIN". 
   |--- Everyone is in uncertain state so the decision taken is abort.
   |--- When the coordinator wakes up, there is an inconsistency in the decision taken by the coordinator and the decision taken by the participants. Thus we are
   	violating AC1 (All processes that reach a decision reach the same one). Also, the decision cannot be reverted without violating AC2 (A process cannot 
	reverse its decision after it has reached one).

Scenario (With logging precommits):
   |--- Coordinator writes Commit to its logs after getting ACKs from everyone, but dies before sending commits to anyone.
   |--- Now the Coordinator dies. All the participants on timeout, run the election protocol whereby electing the operational process with the lowest PID as the new
        coordinator. The state of all participants is "COMMITABLE". 
   |--- At this point, all the participants die, thereby inducing total failure. 
   |--- Now all the participants start recovering. 
   |--- Since a precommit was logged, all recovered participants know that everyone had voted "yes" and that the coordinator was ready to send a commit.
   |--- Therefore, the recovered participants decide to "COMMIT".
   |--- When the coordinator wakes up, there is no inconsistency in the decision taken by the coordinator and the decision taken by the participants. Thus we are
   	not violating AC1 (All processes that reach a decision reach the same one). 

 
