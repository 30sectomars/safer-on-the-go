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

data.overall <- subset(data.raw, QuestionType == "Overall")
question.best <- subset(data.overall, Question == "Which input method did you find the best overall?")
question.easiest <- subset(data.overall, Question == "Which input method did you find the easiest to use?")
question.safest <- subset(data.overall, Question == "Which input method did you feel the safest using?")
question.accurate <- subset(data.overall, Question == "Which input method allowed you to select the targets most accurately?")
question.quickest <- subset(data.overall, Question == "Which input method allowed you to access the targets the quickest?")
question.preferred <- subset(data.overall, Question == "Which input method would you prefer to use if you had to use it regularly on a bike?")

# Plot best
df <- data.frame(Best = factor(question.best$Answer, levels = c("Analog Stick (Remote)", "Buttons (Remote)", "Direct Touch (Tablet)", "Indirect Touch (Remote)")))

p1 <- ggplot(df, aes(x = Best, fill = Best)) +
  geom_bar(color = "black") +
  scale_fill_manual(values = c("aquamarine", "bisque", "coral", "darkolivegreen"), drop = FALSE) +
  scale_y_continuous(breaks = seq(0, 16, 2), limits = c(0, 16)) +
  scale_x_discrete(
    drop = FALSE,
    labels = c(
      "Analog Stick (Remote)" = "Analog stick",
      "Buttons (Remote)" = "Buttons",
      "Direct Touch (Tablet)" = "Direct touch",
      "Indirect Touch (Remote)" = "Indirect touch"
    )
  ) + 
  labs(title = "Best input method",
       x = "Method",
       y = "Count") +
  theme_minimal() +
  theme(
    text = element_text(size = 16),
    plot.title = element_text(size = 16, hjust = 0.5),
    legend.title = element_blank(),
    legend.position = "none",
    axis.title.x = element_blank(),
    axis.title.y = element_text(size = 12)
  )

#print(p1)

# Plot easiest
df <- data.frame(Easiest = factor(question.easiest$Answer, levels = c("Analog Stick (Remote)", "Buttons (Remote)", "Direct Touch (Tablet)", "Indirect Touch (Remote)")))

p2 <- ggplot(df, aes(x = Easiest, fill = Easiest)) +
  geom_bar(color = "black") +
  scale_fill_manual(values = c("aquamarine", "bisque", "coral", "darkolivegreen"), drop = FALSE) +
  scale_y_continuous(breaks = seq(0, 16, 2), limits = c(0, 16)) +
  scale_x_discrete(
    drop = FALSE,
    labels = c(
      "Analog Stick (Remote)" = "Analog stick",
      "Buttons (Remote)" = "Buttons",
      "Direct Touch (Tablet)" = "Direct touch",
      "Indirect Touch (Remote)" = "Indirect touch"
    )
  ) + 
  labs(title = "Easiest input method",
       x = "Method",
       y = "Count") +
  theme_minimal() +
  theme(
    text = element_text(size = 16),
    plot.title = element_text(size = 16, hjust = 0.5),
    legend.title = element_blank(),
    legend.position = "none",
    axis.title.x = element_blank(),
    axis.title.y = element_blank()
  )

#print(p2)

# Plot safest
df <- data.frame(Safest = factor(question.safest$Answer, levels = c("Analog Stick (Remote)", "Buttons (Remote)", "Direct Touch (Tablet)", "Indirect Touch (Remote)")))

p3 <- ggplot(df, aes(x = Safest, fill = Safest)) +
  geom_bar(color = "black") +
  scale_fill_manual(values = c("aquamarine", "bisque", "coral", "darkolivegreen"), drop = FALSE) +
  scale_y_continuous(breaks = seq(0, 16, 2), limits = c(0, 16)) +
  scale_x_discrete(
    drop = FALSE,
    labels = c(
      "Analog Stick (Remote)" = "Analog stick",
      "Buttons (Remote)" = "Buttons",
      "Direct Touch (Tablet)" = "Direct touch",
      "Indirect Touch (Remote)" = "Indirect touch"
    )
  ) +
  labs(title = "Safest input method",
       x = "Method",
       y = "Count") +
  theme_minimal() +
  theme(
    text = element_text(size = 16),
    plot.title = element_text(size = 16, hjust = 0.5),
    legend.title = element_blank(),
    legend.position = "none",
    axis.title.x = element_blank(),
    axis.title.y = element_blank()
  )

#print(p3)

# Plot accurate
df <- data.frame(Accurate = factor(question.accurate$Answer, levels = c("Analog Stick (Remote)", "Buttons (Remote)", "Direct Touch (Tablet)", "Indirect Touch (Remote)")))

p4 <- ggplot(df, aes(x = Accurate, fill = Accurate)) +
  geom_bar(color = "black") +
  scale_fill_manual(values = c("aquamarine", "bisque", "coral", "darkolivegreen"), drop = FALSE) +
  scale_y_continuous(breaks = seq(0, 12, 2), limits = c(0, 12)) +
  scale_x_discrete(
    drop = FALSE,
    labels = c(
      "Analog Stick (Remote)" = "Analog stick",
      "Buttons (Remote)" = "Buttons",
      "Direct Touch (Tablet)" = "Direct touch",
      "Indirect Touch (Remote)" = "Indirect touch"
    )
  ) + 
  labs(title = "Most accurate input method",
       x = "Method",
       y = "Count") +
  theme_minimal() +
  theme(
    text = element_text(size = 16),
    plot.title = element_text(size = 16, hjust = 0.5),
    legend.title = element_blank(),
    legend.position = "none",
    axis.title.x = element_blank(),
    axis.title.y = element_text(size = 12)
  )

#print(p4)

# Plot quickest
df <- data.frame(Quickest = factor(question.quickest$Answer, levels = c("Analog Stick (Remote)", "Buttons (Remote)", "Direct Touch (Tablet)", "Indirect Touch (Remote)")))

p5 <- ggplot(df, aes(x = Quickest, fill = Quickest)) +
  geom_bar(color = "black") +
  scale_fill_manual(values = c("aquamarine", "bisque", "coral", "darkolivegreen"), drop = FALSE) +
  scale_y_continuous(breaks = seq(0, 12, 2), limits = c(0, 12)) +
  scale_x_discrete(
    drop = FALSE,
    labels = c(
      "Analog Stick (Remote)" = "Analog stick",
      "Buttons (Remote)" = "Buttons",
      "Direct Touch (Tablet)" = "Direct touch",
      "Indirect Touch (Remote)" = "Indirect touch"
    )
  ) +
  labs(title = "Quickest input method",
       x = "Method",
       y = "Count") +
  theme_minimal() +
  theme(
    text = element_text(size = 16),
    plot.title = element_text(size = 16, hjust = 0.5),
    legend.title = element_blank(),
    legend.position = "none",
    axis.title.x = element_blank(),
    axis.title.y = element_blank()
  )

#print(p5)

# Plot preferred
df <- data.frame(Preferred = factor(question.preferred$Answer, levels = c("Analog Stick (Remote)", "Buttons (Remote)", "Direct Touch (Tablet)", "Indirect Touch (Remote)")))

p6 <- ggplot(df, aes(x = Preferred, fill = Preferred)) +
  geom_bar(color = "black") +
  scale_fill_manual(values = c("aquamarine", "bisque", "coral", "darkolivegreen"), drop = FALSE) +
  scale_y_continuous(breaks = seq(0, 12, 2), limits = c(0, 12)) +
  scale_x_discrete(
    drop = FALSE,
    labels = c(
      "Analog Stick (Remote)" = "Analog stick",
      "Buttons (Remote)" = "Buttons",
      "Direct Touch (Tablet)" = "Direct touch",
      "Indirect Touch (Remote)" = "Indirect touch"
    )
  ) +
  labs(title = "Preferred input method",
       x = "Method",
       y = "Count") +
  theme_minimal() +
  theme(
    text = element_text(size = 16),
    plot.title = element_text(size = 16, hjust = 0.5),
    legend.title = element_blank(),
    legend.position = "none",
    axis.title.x = element_blank(),
    axis.title.y = element_blank()
  )

#print(p6)

# Combine and save plots
data.plots1 <- p1 + p2 + p3
plot(data.plots1)
ggsave(file.path("overall_1_3.pdf"), plot=data.plots1, width = 15, height = 7, limitsize = FALSE)
data.plots2 <- p4 + p5 + p6
plot(data.plots2)
ggsave(file.path("overall_4_6.pdf"), plot=data.plots2, width = 15, height = 7, limitsize = FALSE)
