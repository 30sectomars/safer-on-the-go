library(ARTool)
library(emmeans)
library(tidyverse)
library(plyr)      
library(ggplot2)
library(purrr)

ALPHA <- 0.05

data.raw <- read.delim("../all_questionair_merged.txt", header = T)

data.raw$ProbID <- factor(data.raw$ProbID)
data.raw$Question <- factor(data.raw$Question)
data.raw$QuestionType <- factor(data.raw$QuestionType)
data.raw$Method <- factor(data.raw$Method)

#data.analog.stick <- data.raw %>% filter(Method == "Analog Stick (Remote)")
#data.buttons <- data.raw %>% filter(Method == "Buttons (Remote)")
#data.direct.touch <- data.raw %>% filter(Method == "Direct Touch (Tablet)")
#data.indirect.touch <- data.raw %>% filter(Method == "Indirect Touch (Remote)")

data.analog.stick.q1 <- data.raw %>% filter(Method == "Analog Stick (Remote)" & Question == "How safe did you feel while operating the device?")
data.analog.stick.q2 <- data.raw %>% filter(Method == "Analog Stick (Remote)" & Question == "How comfortable did you feel?")
data.analog.stick.q3 <- data.raw %>% filter(Method == "Analog Stick (Remote)" & Question == "I found it easy to understand how to use the input method.")
data.analog.stick.q4 <- data.raw %>% filter(Method == "Analog Stick (Remote)" & Question == "I could reach the targets precisely with the input method.")
data.analog.stick.q5 <- data.raw %>% filter(Method == "Analog Stick (Remote)" & Question == "I could focus on cycling while using the input method.")
data.analog.stick.q6 <- data.raw %>% filter(Method == "Analog Stick (Remote)" & Question == "The input felt physically effortless.")
data.analog.stick.q7 <- data.raw %>% filter(Method == "Analog Stick (Remote)" & Question == "The remote control did not interfere with my grip or steering.")
data.analog.stick.q8 <- data.raw %>% filter(Method == "Analog Stick (Remote)" & Question == "I enjoyed using this remote control.")

shapiro_tests_analog_stick <- c(shapiro.test(as.numeric(data.analog.stick.q1$Answer))$p.value,
                                shapiro.test(as.numeric(data.analog.stick.q2$Answer))$p.value,
                                shapiro.test(as.numeric(data.analog.stick.q3$Answer))$p.value,
                                shapiro.test(as.numeric(data.analog.stick.q4$Answer))$p.value,
                                shapiro.test(as.numeric(data.analog.stick.q5$Answer))$p.value,
                                shapiro.test(as.numeric(data.analog.stick.q6$Answer))$p.value,
                                shapiro.test(as.numeric(data.analog.stick.q7$Answer))$p.value,
                                shapiro.test(as.numeric(data.analog.stick.q8$Answer))$p.value
                               )
alpha_analog_stick <- keep(shapiro_tests_analog_stick, function(x) x > ALPHA)
alpha_analog_stick_count <- length(alpha_analog_stick)

data.buttons.q1 <- data.raw %>% filter(Method == "Buttons (Remote)" & Question == "How safe did you feel while operating the device?")
data.buttons.q2 <- data.raw %>% filter(Method == "Buttons (Remote)" & Question == "How comfortable did you feel?")
data.buttons.q3 <- data.raw %>% filter(Method == "Buttons (Remote)" & Question == "I found it easy to understand how to use the input method.")
data.buttons.q4 <- data.raw %>% filter(Method == "Buttons (Remote)" & Question == "I could reach the targets precisely with the input method.")
data.buttons.q5 <- data.raw %>% filter(Method == "Buttons (Remote)" & Question == "I could focus on cycling while using the input method.")
data.buttons.q6 <- data.raw %>% filter(Method == "Buttons (Remote)" & Question == "The input felt physically effortless.")
data.buttons.q7 <- data.raw %>% filter(Method == "Buttons (Remote)" & Question == "The remote control did not interfere with my grip or steering.")
data.buttons.q8 <- data.raw %>% filter(Method == "Buttons (Remote)" & Question == "I enjoyed using this remote control.")

shapiro_tests_buttons <- c(shapiro.test(as.numeric(data.buttons.q1$Answer))$p.value,
                           shapiro.test(as.numeric(data.buttons.q2$Answer))$p.value,
                           shapiro.test(as.numeric(data.buttons.q3$Answer))$p.value,
                           shapiro.test(as.numeric(data.buttons.q4$Answer))$p.value,
                           shapiro.test(as.numeric(data.buttons.q5$Answer))$p.value,
                           shapiro.test(as.numeric(data.buttons.q6$Answer))$p.value,
                           shapiro.test(as.numeric(data.buttons.q7$Answer))$p.value,
                           shapiro.test(as.numeric(data.buttons.q8$Answer))$p.value
                          )
alpha_buttons <- keep(shapiro_tests_buttons, function(x) x > ALPHA)
alpha_buttons_count <- length(alpha_buttons)

data.indirect.touch.q1 <- data.raw %>% filter(Method == "Indirect Touch (Remote)" & Question == "How safe did you feel while operating the device?")
data.indirect.touch.q2 <- data.raw %>% filter(Method == "Indirect Touch (Remote)" & Question == "How comfortable did you feel?")
data.indirect.touch.q3 <- data.raw %>% filter(Method == "Indirect Touch (Remote)" & Question == "I found it easy to understand how to use the input method.")
data.indirect.touch.q4 <- data.raw %>% filter(Method == "Indirect Touch (Remote)" & Question == "I could reach the targets precisely with the input method.")
data.indirect.touch.q5 <- data.raw %>% filter(Method == "Indirect Touch (Remote)" & Question == "I could focus on cycling while using the input method.")
data.indirect.touch.q6 <- data.raw %>% filter(Method == "Indirect Touch (Remote)" & Question == "The input felt physically effortless.")
data.indirect.touch.q7 <- data.raw %>% filter(Method == "Indirect Touch (Remote)" & Question == "The remote control did not interfere with my grip or steering.")
data.indirect.touch.q8 <- data.raw %>% filter(Method == "Indirect Touch (Remote)" & Question == "I enjoyed using this remote control.")

shapiro_tests_indirect_touch <- c(shapiro.test(as.numeric(data.indirect.touch.q1$Answer))$p.value,
                                  shapiro.test(as.numeric(data.indirect.touch.q2$Answer))$p.value,
                                  shapiro.test(as.numeric(data.indirect.touch.q3$Answer))$p.value,
                                  shapiro.test(as.numeric(data.indirect.touch.q4$Answer))$p.value,
                                  shapiro.test(as.numeric(data.indirect.touch.q5$Answer))$p.value,
                                  shapiro.test(as.numeric(data.indirect.touch.q6$Answer))$p.value,
                                  shapiro.test(as.numeric(data.indirect.touch.q7$Answer))$p.value,
                                  shapiro.test(as.numeric(data.indirect.touch.q8$Answer))$p.value
                                 )
alpha_indirect_touch <- keep(shapiro_tests_indirect_touch, function(x) x > ALPHA)
alpha_indirect_touch_count <- length(alpha_indirect_touch)

data.direct.touch.q1 <- data.raw %>% filter(Method == "Direct Touch (Tablet)" & Question == "How safe did you feel while operating the device?")
data.direct.touch.q2 <- data.raw %>% filter(Method == "Direct Touch (Tablet)" & Question == "How comfortable did you feel?")
data.direct.touch.q3 <- data.raw %>% filter(Method == "Direct Touch (Tablet)" & Question == "I could focus on cycling while using the input method.")
data.direct.touch.q4 <- data.raw %>% filter(Method == "Direct Touch (Tablet)" & Question == "The input felt physically effortless.")
data.direct.touch.q5 <- data.raw %>% filter(Method == "Direct Touch (Tablet)" & Question == "I found it easy to understand how to use the input method.")
data.direct.touch.q6 <- data.raw %>% filter(Method == "Direct Touch (Tablet)" & Question == "Did you feel that using touch input required you to look away from the road/environment too much?")
data.direct.touch.q7 <- data.raw %>% filter(Method == "Direct Touch (Tablet)" & Question == "I could reach the targets precisely with the input method.")

shapiro_tests_direct_touch <- c(shapiro.test(as.numeric(data.direct.touch.q1$Answer))$p.value,
                                shapiro.test(as.numeric(data.direct.touch.q2$Answer))$p.value,
                                shapiro.test(as.numeric(data.direct.touch.q3$Answer))$p.value,
                                shapiro.test(as.numeric(data.direct.touch.q4$Answer))$p.value,
                                shapiro.test(as.numeric(data.direct.touch.q5$Answer))$p.value,
                                shapiro.test(as.numeric(data.direct.touch.q6$Answer))$p.value,
                                shapiro.test(as.numeric(data.direct.touch.q7$Answer))$p.value
                               )
alpha_direct_touch <- keep(shapiro_tests_direct_touch, function(x) x > ALPHA)
alpha_direct_touch_count <- length(alpha_direct_touch)

# no data got nearly 24 alpha's above 0.05 --> All data is not normally distributed

