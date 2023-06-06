#  YourMagicChatBot

This bot is an everyday oracle, which allows users to receive a daily prediction and a sacred card. Each user has three attempts per week, but no more than one per day. On Thursdays, the bot functions as an answer from the universe. The user must ask themselves a question and click the "I'm ready" button to receive a response from the universe. The bot is built using Spring Boot and uses a PostgreSQL database. To support emojis, the emoji-java library was used, and for logging, the Slf4j facade was utilized. To prevent spam, the Cache pattern was implemented. The system.properties file is required for proper functioning on Heroku, as Java 17 is used with lambda expressions in switch-case statements. All sacred cards are stored in the database, as Telegram only stores images for a limited time, which cannot guarantee that they will not be lost. The bot was made using WebHook, ngrok was used for testing.

Please note that this bot is a copy of my commercial bot that is currently in active use.

##  Daily predictions

#### Daily divination includes random sacred card and prediction


![](https://od.lk/s/NDZfMzY0OTI0Njhf/get.jpg)

If you already spent a try today

![](https://od.lk/s/NDZfMzcxMTAzODRf/Screenshot_2.png)

If you spent 3 weekly attempts

![](https://od.lk/s/NDZfMzcxMTAxODBf/Screenshot_1.png)

To add a new prediction with a sacred card, you need to be an admin and simply send a picture with a description to the bot.
If everything is added successfully, the bot will notify you about it.

![](https://od.lk/s/NDZfMzcxMTA2Mzhf/Screenshot_3.png)

If you are not an admin, the bot will reply that the command is not recognized.

![](https://od.lk/s/NDZfMzcxMTA5NTZf/Screenshot_4.png)

If you forget to send a text with a picture, the bot will also write to you about it.
If you accidentally sent a picture with a worng description to the bot, or made a mistake in the description - use the /deleteLastCard command, it will delete the last card with a prediction

Sending card with no caption    |   deleting the previous card using the /deleteLastCard command
:-------------------------:|:-------------------------:
![](https://od.lk/s/NDZfMzcxMTEyNzVf/Screenshot_5.png)|![](https://od.lk/s/NDZfMzcxMTEyODRf/Screenshot_6.png)

To let the bot know that you are an admin, just add your chatId to the line owners of the application.properties file, separated with coma and space.

Example:
![](https://od.lk/s/NDZfMzcxMTEzNDBf/Screenshot_7.png)

I also want to note that the bot sends everyone a notification about updating attempts on Sunday.

##  Universe Answers
The Universe Answers Day reminder comes at 9am at Thursday, but only to those users, who haven't used their attempt yet.
If the user spent their attempt before, he won't get the reminder.

If you put this button not on Thursday:

Example:
![](https://od.lk/s/NDZfMzcxMTQyNzhf/Screenshot_15.png)

If you put this button on Thursday:
![](https://od.lk/s/NDZfMzcxMTQyNzVf/Screenshot_12.png)

After pressing the "I'm ready" button, the message changes to a prediction.
#### Prediction can be added with the /sendUniverse TEXT command.

Example:
![](https://od.lk/s/NDZfMzcxMTQyNzdf/Screenshot_14.png)

If you press the "I'm ready" button after Thursday, the bot will write that it's too late.

Example:
![](https://od.lk/s/NDZfMzcxMTQyNzZf/Screenshot_13.png)

If you try to get the second answer of the Universe in one Thursday:
![](https://od.lk/s/NDZfMzcxMTQzOTBf/Screenshot_19.png)


In the future I plan to integrate ChatGPT here.

##  Spam protection
The bot also has spam protection. It will respond to 1 message per second from users, this is the telegram requirement for the API to work correctly.
If you send a lot of messages to the bot at the same time, it will respond to the first message, the rest will be ignored.
The message that the user is spamming will be displayed in the logs along with his chatID.

Example:
Spamming   |   Results in logs
:-------------------------:|:-------------------------:
![](https://od.lk/s/NDZfMzcxMTI1ODNf/Screenshot_9.png)|![](https://od.lk/s/NDZfMzcxMTI1ODRf/Screenshot_10.png)

The anti-spam system is implemented using the cache pattern, I also had the idea to use Redis, because it is a fast key-value database. But in order to save money, I chose the cache to implement the anti-spam system.

## Sending messages to users

There are 2 types for mailing: with or without a picture.

To simply send a newsletter to all users, you need to be an administrator and send the /sendAll TEXT command to the bot.

To send a newsletter with a picture, you need to send a picture and send /sendAll TEXT to its description

Also, after any type of mailing, the number of users who received the message comes.

Example:
Admin do mailing |   User get it
:-------------------------:|:-------------------------:
![](https://od.lk/s/NDZfMzcxMTQzNzNf/Screenshot_16.png)|![](https://od.lk/s/NDZfMzcxMTQzNzRf/Screenshot_17.png)

### Сonclusion

These are not all the functions of the bot, I have described only the main ones.
The bot works well, all exceprions are handled, customs exceptions have also been added, they also their functions well.
Logs record all errors, mailings and in general - all the information necessary for the developer.
I tried to do everything according to the SOLID principles, but please do not judge me strictly, this is my first commercial project.

I plan to add integration with ChatGPT, a personal collection for each user, localization of the bot for English speaking users.

Thank you for your attention, for any questions - write to me in telegram or LinkedIn specified in the profile.
