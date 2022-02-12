# Bazinga-2021-2022

Sept 22, 2021 - First update

              - Created basic teleOp, hardware map, and auto 

Oct 27, 2021  - Forgot to upload the code from the previous weeks

              - Finished almost all of teleop
              
              - Teleop works
              
              - Added a fifth wheel called "mid"
              
Nov 6, 2021   - Teleop and Autonomous
              
              - Strafing is complete (All of teleop is done)
              
              - Autonomous with encoders is starting 
              
              - Tarek is working on AutoAudienceBlue without encoders

Nov 10, 2021  - Testing

              - Forwards and backwards was reverse, issue fixed by making y positive in Teleop. Other than this, Teleop is working fine
              
              - Strafing works, only problems are mechanical ones
              
              - Autonomous with encoders is having issues with flipping the the right and left motors as well as the calculations to go a certain amount of distance.

Nov 13, 2021  - Autonomous with encoders

              - Hannah figured out how to incorporate encoders into the autonomous that Tarek created
              
              - Haven't been able to test any of the autonomous' due to the robot having mechanical complications
              
              - Changed some teleop controls

Nov 16, 2021  - Test Autonomous

              - The motors stop really early and moves very little
              
              - Might not use encoders for the competition this saturday
              
              - Will try to fix everything in the next meeting (tomorrow)
              
Nov 17, 2021  - Fix Encoders

              - Found the problem to driving, it goes a little too far but good enough
              
              - Instead of adding the inches times the Counts per motor rev, we are subtracting them to go in the correct direction
              
Nov 19, 2021  - Test Autonomous for competition

              - AutoAudienceRedEncoders works very well, exact measurements with only a small magin of error
              
              - Just a few more fine tuning things and it will work very well (we decided not to do odometry or use a camera for the competition tomorrow)
 
              - AutoAudienceBlueEncoders is still a process and we do not have a far side auto
              
              - Added a lift encoder
              
              - In teleop, we added a reverse, so whenever a driver 1 presses bumper, it will reverse the driving mechanism
              
Dec 1, 2021   - Camera and competition summary
    
              - Competition was on November 20th, programmed all four autonomous' during the competition
              
              - The far autonomous' just move forward and back up into the warehouse
              
              - Trying to incorporate Vuforia and TFOD, haven't tested yet, needs a lot of work
              
              - Added a claw for the capstone (Teleop)

Dec 15, 2021  - Camera Testing

              - Tested the camera for the first time and it works, it detects objects and labels them
              
              - To find the values, we took multiple states of data and put it in an excel file to see the limits and the positions of the duck/capstone
           
              - Will try to test more and implement it into the actual autonomous'

Dec 17, 2021  - Camera in Autonomous
     
              - Implemented the webcam program into the red autonomous on the carousel side
              
              - Tested the program and had a couple issues but fixed it by reseting the encoder positions to 0
              
Jan 2, 2022   - Happy New Year :D (Winter Break)

              - Took out the "Marker" Label for the Webcam in AutoRedAudienceWebcam
              
              - Commented out the original function to move the lift in teleop: Initially, holding a button would make the lift rise until the button is released. Now, pushing the dpad each time incrementally raises the lift on the robot and the dpad's right and left buttons make the lift go all the way up or all the way down, respectively
              
              - Added a To-Do list file so other programmers on the team have something to work on
              
              - Added all of the files into a new branch on Github, which is java/org/firstinspires/ftc/teamcode

Jan 12, 2022  - Fixed AutoRedFarWebcam
            
              - Added code to AutoRedFarWebcam and now just have to test it
              
              - Added back the joystick control for the lift in Teleop

              - We now have 5 inch wheels on the robot
              
Jan 15, 2022  - New Wheels = errors
  
              - With the new wheels, strafing and just moving in autonomous in general is messed up
              
              - Tried manually doing strafing, which worked
              
              - A lot of friction by wheels on the ground and the plate on the robot

Jan 18, 2022  - Test Wheels

              - Created a new autonomous that tests the wheels to make sure that they work in all directions
              
              - the program makes the robot move forward, then backward, then strafes left and right

Jan 22, 2022  - Rewire robot

              - Robot was rewired so that all of the drive train motors were on one hub (expansion hub)
              
              - The two front wheels were reversed so we re-reversed them
              
Jan 26, 2022  - Fixed wheels and test auto

              - We found that the problem was that two of the wheels were wired reversed and it was re-reversed so now in the configuration and the program, we reversed them
              
              - Double negative = positive
              
              - We ran the wheels one-by-one and found out that the front two wheels were reversed
              
              - After we fixed the wheels, we tested auto red audience and just started tuning the measurements.
              
Feb 5, 2022   - Fixed Tape mechanism

              - Edited values for the tape mechanism for endgame
              
              - Added last stop (n)

              - GOING TO STATES
              
              - WON CONTROL AWARD WOOOO
              
Feb 7, 2022   - Tune tape measure mechanism

              - Just edited the values for the tape measure mechanism a little more for the driver
              
              - Mechanical problems
              
              - The dump keeps getting stuck in teleop

Feb 11, 2022  - Auto before states

              - Fixed the stuck in dump problem
              
              - Test and tuned the RedAudience program
              
              - Made it strafe at the end and then move forward to fully park
              
              - Tested both Far programs, tuned them a little bit, both worked, and can be used
              
              - The big plate on the side will always be touching the wall for the autonomous' except BlueAudience
