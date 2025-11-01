library(ARTool)
library(emmeans)
library(tidyverse)
library(plyr)      
library(ggplot2)
library(purrr)

ALPHA <- 0.05

data.raw <- read.delim("../all_experimental_merged.txt", header = T)

data.raw$ProbID <- factor(data.raw$ProbID)
data.raw$Method <- factor(data.raw$Method)
data.raw$Layout <- factor(data.raw$Layout)

data.analog.vertical <- data.raw %>% filter(Method == "Analogue Stick" & Layout == "Vertical")
data.analog.horizontal <- data.raw %>% filter(Method == "Analogue Stick" & Layout == "Horizontal")
data.analog.2D <- data.raw %>% filter(Method == "Analogue Stick" & Layout == "2D")

data.buttons.vertical <- data.raw %>% filter(Method == "Buttons" & Layout == "Vertical")
data.buttons.horizontal <- data.raw %>% filter(Method == "Buttons" & Layout == "Horizontal")
data.buttons.2D <- data.raw %>% filter(Method == "Buttons" & Layout == "2D")

data.direct.touch.vertical <- data.raw %>% filter(Method == "Direct Touch" & Layout == "Vertical")
data.direct.touch.horizontal <- data.raw %>% filter(Method == "Direct Touch" & Layout == "Horizontal")
data.direct.touch.2D <- data.raw %>% filter(Method == "Direct Touch" & Layout == "2D")

data.indirect.touch.vertical <- data.raw %>% filter(Method == "Indirect Touch" & Layout == "Vertical")
data.indirect.touch.horizontal <- data.raw %>% filter(Method == "Indirect Touch" & Layout == "Horizontal")
data.indirect.touch.2D <- data.raw %>% filter(Method == "Indirect Touch" & Layout == "2D")

shapiro_tests_task_completion_time <- c(shapiro.test(as.numeric(data.analog.vertical$TaskCompletionTime))$p.value,
                                       shapiro.test(as.numeric(data.analog.horizontal$TaskCompletionTime))$p.value,
                                       shapiro.test(as.numeric(data.analog.2D$TaskCompletionTime))$p.value,
                            
                                       shapiro.test(as.numeric(data.buttons.vertical$TaskCompletionTime))$p.value,
                                       shapiro.test(as.numeric(data.buttons.horizontal$TaskCompletionTime))$p.value,
                                       shapiro.test(as.numeric(data.buttons.2D$TaskCompletionTime))$p.value,
                                      
                                       shapiro.test(as.numeric(data.direct.touch.vertical$TaskCompletionTime))$p.value,
                                       shapiro.test(as.numeric(data.direct.touch.horizontal$TaskCompletionTime))$p.value,
                                       shapiro.test(as.numeric(data.direct.touch.2D$TaskCompletionTime))$p.value,
                                      
                                       shapiro.test(as.numeric(data.indirect.touch.vertical$TaskCompletionTime))$p.value,
                                       shapiro.test(as.numeric(data.indirect.touch.horizontal$TaskCompletionTime))$p.value,
                                       shapiro.test(as.numeric(data.indirect.touch.2D$TaskCompletionTime))$p.value
                                      )
alpha_task_completion_time <- keep(shapiro_tests_task_completion_time, function(x) x > ALPHA)
alpha_task_completion_time_count <- length(alpha_task_completion_time)

shapiro_tests_error_rate <- c(shapiro.test(as.numeric(data.analog.vertical$ErrorRate))$p.value,
                             #shapiro.test(as.numeric(data.analog.horizontal$ErrorRate))$p.value, #Alle Werte = 0!
                             shapiro.test(as.numeric(data.analog.2D$ErrorRate))$p.value,
                             
                             shapiro.test(as.numeric(data.buttons.vertical$ErrorRate))$p.value,
                             shapiro.test(as.numeric(data.buttons.horizontal$ErrorRate))$p.value,
                             shapiro.test(as.numeric(data.buttons.2D$ErrorRate))$p.value,
                             
                             shapiro.test(as.numeric(data.direct.touch.vertical$ErrorRate))$p.value,
                             shapiro.test(as.numeric(data.direct.touch.horizontal$ErrorRate))$p.value,
                             shapiro.test(as.numeric(data.direct.touch.2D$ErrorRate))$p.value,
                             
                             shapiro.test(as.numeric(data.indirect.touch.vertical$ErrorRate))$p.value,
                             shapiro.test(as.numeric(data.indirect.touch.horizontal$ErrorRate))$p.value,
                             shapiro.test(as.numeric(data.indirect.touch.2D$ErrorRate))$p.value
                            )
alpha_error_rate <- keep(shapiro_tests_error_rate, function(x) x > ALPHA)
alpha_error_rate_count <- length(alpha_error_rate)

shapiro_tests_overshoot <- c(shapiro.test(as.numeric(data.analog.vertical$Overshoot))$p.value,
                            shapiro.test(as.numeric(data.analog.horizontal$Overshoot))$p.value,
                            shapiro.test(as.numeric(data.analog.2D$Overshoot))$p.value,
                           
                            shapiro.test(as.numeric(data.buttons.vertical$Overshoot))$p.value,
                            shapiro.test(as.numeric(data.buttons.horizontal$Overshoot))$p.value,
                            shapiro.test(as.numeric(data.buttons.2D$Overshoot))$p.value,
                           
                            shapiro.test(as.numeric(data.indirect.touch.vertical$Overshoot))$p.value,
                            shapiro.test(as.numeric(data.indirect.touch.horizontal$Overshoot))$p.value,
                            shapiro.test(as.numeric(data.indirect.touch.2D$Overshoot))$p.value
                           )

alpha_overshoot <- keep(shapiro_tests_overshoot, function(x) x > ALPHA)
alpha_overshoot_count <- length(alpha_overshoot)

shapiro_tests_overshoot_duration <- c(shapiro.test(as.numeric(data.analog.vertical$OvershootTime))$p.value,
                                     shapiro.test(as.numeric(data.analog.horizontal$OvershootTime))$p.value,
                                     shapiro.test(as.numeric(data.analog.2D$OvershootTime))$p.value,
                                    
                                     shapiro.test(as.numeric(data.buttons.vertical$OvershootTime))$p.value,
                                     shapiro.test(as.numeric(data.buttons.horizontal$OvershootTime))$p.value,
                                     shapiro.test(as.numeric(data.buttons.2D$OvershootTime))$p.value,
                                    
                                     shapiro.test(as.numeric(data.indirect.touch.vertical$OvershootTime))$p.value,
                                     shapiro.test(as.numeric(data.indirect.touch.horizontal$OvershootTime))$p.value,
                                     shapiro.test(as.numeric(data.indirect.touch.2D$OvershootTime))$p.value
                                    )

alpha_overshoot_duration <- keep(shapiro_tests_overshoot_duration, function(x) x > ALPHA)
alpha_overshoot_duration_count <- length(alpha_overshoot_duration)

# no data got nearly 24 alpha's above 0.05 --> All data is not normally distributed
