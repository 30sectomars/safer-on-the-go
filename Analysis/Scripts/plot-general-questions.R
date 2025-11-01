library(ARTool)
library(emmeans)
library(tidyverse)
library(tidyr)
library(dplyr)
library(plyr)
library(ggplot2)
library(likert)
library(patchwork)
source("HelperScripts/summary.r")
source("HelperScripts/grid_arrange_shared_legend.r")

data.raw <- read.delim("../all_questionair_merged.txt", header = T)
data.raw$ProbID <- factor(data.raw$ProbID)
data.raw$Question <- factor(data.raw$Question)
data.raw$QuestionType <- factor(data.raw$QuestionType)
data.raw$Method <- factor(data.raw$Method)

data.general <- subset(data.raw, QuestionType == "General")
question.age <- subset(data.general, Question == "How old are you?")
question.gender <- subset(data.general, Question == "What is your gender?")
question.frequency <- subset(data.general, Question == "How often do you ride a bicycle?")
question.bicycletypes <- subset(data.general, Question == "Which types of bicycles have you ridden so far?")
question.phoneUsage <- subset(data.general, Question == "How often do you use a smartphone while riding a bicycle?")
question.usage <- subset(data.general, Question == "If you used a smartphone while riding a bicycle, what did you do with it?")
question.road <- subset(data.general, Question == "In what environment did you use your smartphone while riding your bicycle?")
question.control <- subset(data.general, Question == "How did you use a smartphone while riding your bicycle?")

# Plot age and gender
df <- merge(data.frame(ProbID = question.age$ProbID, Age = as.numeric(question.age$Answer)), data.frame(ProbID = question.gender$ProbID, Gender = question.gender$Answer), by = "ProbID")
df$AgeGroup <- cut(df$Age, breaks = c(seq(18, 67, by = 5), 68), right = FALSE, labels = paste(seq(18, 63, by = 5), seq(22, 67, by = 5), sep = "-"))

p1 <- ggplot(df, aes(x = AgeGroup, fill = Gender)) +
      geom_bar(position = "stack", color = "black") +
      scale_y_continuous(breaks = seq(0, 10, 2), limits = c(0, 9)) +
      scale_x_discrete(drop = FALSE) + 
      labs(title = "Age and gender",
           x = "Age",
           y = "Count") +
      theme_minimal() +
      theme(
        text = element_text(size = 16),
        plot.title = element_text(size = 16, hjust = 0.5),
        legend.title = element_text(size = 12),
        axis.title.x = element_blank(),
        axis.title.y = element_text(size = 12)
      )

print(p1)

# Plot frequency riding
df <- data.frame(Frequency = factor(question.frequency$Answer, levels = c("Never", "At least once a year", "At least once a month", "At least once a week", "(Almost) Every day")))

p2 <- ggplot(df, aes(x = Frequency)) +
  geom_bar(fill = "darkolivegreen", color = "black") +
  scale_y_continuous(breaks = seq(0, 20, 2), limits = c(0, 19)) +
  labs(title = str_wrap("How often do you ride?", width = 40),
       x = "",
       y = "Count") +
  theme_minimal() +
  theme(
    text = element_text(size = 16),
    plot.title = element_text(size = 16, hjust = 0.5),
    legend.position = "none",
    axis.title.x = element_blank(),
    axis.text.x = element_text(angle = 45, hjust = 1),
    axis.title.y = element_text(size = 12)
  )

#print(p2)

# Plot bicycle types
df <- data.frame(BikeTypes = question.bicycletypes$Answer)
df_long <- df %>% separate_rows(BikeTypes, sep = ";")
df_long$BikeTypes <- factor(df_long$BikeTypes, levels = c("None", "City/Trekking bike", "Mountain bike", "Road/Cyclocross/Gravel bike", "EMTB"))

p3 <- ggplot(df_long, aes(x = BikeTypes)) +
  geom_bar(fill = "darkolivegreen", color = "black") +
  scale_y_continuous(breaks = seq(0, 20, 2), limits = c(0, 19)) +
  labs(title = str_wrap("Which types of bicycles have you ridden so far?", width = 40),
       x = "",
       y = "") +
  theme_minimal() +
  theme(
    text = element_text(size = 16),
    plot.title = element_text(size = 16, hjust = 0.5),
    legend.position = "none",
    axis.title.x = element_blank(),
    axis.text.x = element_text(angle = 45, hjust = 1),
    axis.title.y = element_blank()
  )

#print(p3)

# Plot frequency phone usage
df <- data.frame(Frequency = factor(question.phoneUsage$Answer, levels = c("1", "2", "3", "4", "5")))

p4 <- ggplot(df, aes(x = Frequency)) +
  geom_bar(fill = "darkolivegreen", color = "black") +
  scale_y_continuous(breaks = seq(0, 20, 2), limits = c(0, 19)) +
  scale_x_discrete(
    drop = FALSE,
    labels = c(
      "1" = "Never",
      "2" = "Rarely",
      "3" = "Sometimes",
      "4" = "Often",
      "5" = "Very Often"
    )
  ) + 
  labs(title = str_wrap("How often do you use a smartphone while riding a bicycle?", width = 40),
       x = "",
       y = "") +
  theme_minimal() +
  theme(
    text = element_text(size = 16),
    plot.title = element_text(size = 16, hjust = 0.5),
    legend.position = "none",
    axis.title.x = element_blank(),
    axis.text.x = element_text(angle = 45, hjust = 1),
    axis.title.y = element_blank()
  )

#print(p4)

# Plot usage
df <- data.frame(Usage = question.usage$Answer)
df_long <- df %>% separate_rows(Usage, sep = ";")
df_long$Usage <- factor(df_long$Usage, levels = 
  c(
  "Looked up information on the Internet or for driving directions",
  "Use a smartphone to send text messages",
  "Use a smartphone to read text messages",
  "Use a smartphone to respond a call",
  "Use a smartphone to make a call",
  "Use a smartphone for social media",
  "Use a smartphone for music control"
  ))
df_long <- na.omit(df_long)

p5 <- ggplot(df_long, aes(x = Usage)) +
  geom_bar(fill = "darkolivegreen", color = "black") +
  scale_y_continuous(breaks = seq(0, 18, 2), limits = c(0, 18)) +
  scale_x_discrete(
    drop = FALSE,
    labels = c(
      "Looked up information on the Internet or for driving directions" = "Driving directions",
      "Use a smartphone to send text messages" = "Send messages",
      "Use a smartphone to read text messages" = "Read messages",
      "Use a smartphone to respond a call" = "Respond a call",
      "Use a smartphone to make a call" = "Make a call",
      "Use a smartphone for social media" = "Social media",
      "Use a smartphone for music control" = "Music control"
    )
  ) +
  labs(title = str_wrap("If you used a smartphone while riding a bicycle, what did you do with it?", width = 40),
       x = "",
       y = "Count") +
  theme_minimal() +
  theme(
    text = element_text(size = 16),
    plot.title = element_text(size = 16, hjust = 0.5),
    legend.position = "none",
    axis.title.x = element_blank(),
    axis.text.x = element_text(angle = 45, hjust = 1),
    axis.title.y = element_text(size = 12)
  )

#print(p5)

# Plot road environment
df <- data.frame(Road = question.road$Answer)
df_long <- df %>% separate_rows(Road, sep = ";")
df_long$Road <- factor(df_long$Road, levels = 
                          c(
                            "Main road",
                            "Side road",
                            "Cycle lane / Cycle path",
                            "Country lane",
                            "Dirt road",
                            "Red traffic light"
                          ))
df_long <- na.omit(df_long)

p6 <- ggplot(df_long, aes(x = Road)) +
  geom_bar(fill = "darkolivegreen", color = "black") +
  scale_y_continuous(breaks = seq(0, 18, 2), limits = c(0, 18)) +
  scale_x_discrete(
    drop = FALSE,
    labels = c(
      "Main road" = "Main road",
      "Side road" = "Side road",
      "Cycle lane / Cycle path" = "Cycle path",
      "Country lane" = "Country lane",
      "Dirt road" = "Dirt road",
      "Red traffic light" = "Red light"
    )
  ) +
  labs(title = str_wrap("In what environment did you use your smartphone while riding your bicycle?", width = 40),
       x = "",
       y = "") +
  theme_minimal() +
  theme(
    text = element_text(size = 16),
    plot.title = element_text(size = 16, hjust = 0.5),
    legend.position = "none",
    axis.title.x = element_blank(),
    axis.text.x = element_text(angle = 45, hjust = 1),
    axis.title.y = element_blank()
  )

#print(p6)

# Plot phone control
df <- data.frame(Control = question.control$Answer)
df_long <- df %>% separate_rows(Control, sep = ";")
df_long$Control <- factor(df_long$Control, levels = 
                         c(
                           "Two-handed",
                           "One-handed",
                           "Voice assistent",
                           "Headphone remote control",
                           "External remote control e.g. on the handlebars"
                         ))
df_long <- na.omit(df_long)

p7 <- ggplot(df_long, aes(x = Control)) +
  geom_bar(fill = "darkolivegreen", color = "black") +
  scale_y_continuous(breaks = seq(0, 18, 2), limits = c(0, 18)) +
  scale_x_discrete(
    drop = FALSE,
    labels = c(
      "Two-handed" = "Two-handed",
      "One-handed" = "One-handed",
      "Voice assistent" = "Voice assistent",
      "Headphone remote control" = "Headphone remote control",
      "External remote control e.g. on the handlebars" = "External remote control"
    )
  ) +
  labs(title = str_wrap("How did you use a smartphone while riding your bicycle?", width = 40),
       x = "",
       y = "") +
  theme_minimal() +
  theme(
    text = element_text(size = 16),
    plot.title = element_text(size = 16, hjust = 0.5),
    legend.position = "none",
    axis.title.x = element_blank(),
    axis.text.x = element_text(angle = 45, hjust = 1),
    axis.title.y = element_blank()
  )

#print(p7)

# Combine and save plots
ggsave(file.path("general_age_gender.pdf"), plot=p1, width = 10, height = 5, limitsize = FALSE)
data.plots1 <- p2 + p3 + p4 & coord_cartesian(ylim = c(0, 19))
plot(data.plots1)
ggsave(file.path("general_2_4.pdf"), plot=data.plots1, width = 15, height = 7, limitsize = FALSE)
data.plots2 <- p5 + p6 + p7 & coord_cartesian(ylim = c(0, 18))
plot(data.plots2)
ggsave(file.path("general_5_7.pdf"), plot=data.plots2, width = 15, height = 7, limitsize = FALSE)
