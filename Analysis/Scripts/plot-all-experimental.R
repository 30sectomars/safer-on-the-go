library(ARTool)
library(emmeans)
library(tidyverse)
library(plyr)      
library(ggplot2)
source("HelperScripts/summary.r")
source("HelperScripts/grid_arrange_shared_legend.r")

data.raw <- read.delim("../all_experimental_merged.txt", header = T)

data.raw$ProbID <- factor(data.raw$ProbID)
data.raw$Method <- factor(data.raw$Method)
data.raw$Layout <- factor(data.raw$Layout)

# Plot task completion time
taskCompletionTime <- aggregate(data.raw$TaskCompletionTime,
                      by = list(data.raw$Layout, data.raw$Method, data.raw$ProbID),
                      FUN = sum, na.rm = TRUE)
colnames(taskCompletionTime) <- c("Layout", "Method", "ProbID", "Task completion time")
p1 <- data_summary_plot(
  data = taskCompletionTime,
  varname = "Task completion time",
  groupnames = c("Layout", "Method"),
  fun.error = se,
  p.scale_fill_manual = c("aquamarine", "bisque", "coral", "darkolivegreen"),
  p.basesize = 12
) +
  ggtitle("Task completion time [s]") +
  theme(
    text = element_text(size = 16),
    plot.title = element_text(size = 16, hjust = 0.5),
    legend.title = element_blank(),
    axis.title.x = element_blank(),
    axis.title.y = element_blank()
  )

# Plot error rate
errorRate <- aggregate(data.raw$ErrorRate,
                       by = list(data.raw$Layout, data.raw$Method, data.raw$ProbID),
                       FUN = sum, na.rm = TRUE)
colnames(errorRate) <- c("Layout", "Method", "ProbID", "Error rate")
p2 <- data_summary_plot(
  data = errorRate,
  varname = "Error rate",
  groupnames = c("Layout", "Method"),
  fun.error = se,
  p.scale_fill_manual = c("aquamarine", "bisque", "coral", "darkolivegreen"),
  p.basesize = 12
) +
  ggtitle("Error count") +
  theme(
    text = element_text(size = 16),
    plot.title = element_text(size = 16, hjust = 0.5),
    legend.title = element_blank(),
    axis.title.x = element_blank(),
    axis.title.y = element_blank()
  )

# Plot no target selected
noTargetSelected <- aggregate(data.raw$NoTargetSelected,
                       by = list(data.raw$Layout, data.raw$Method, data.raw$ProbID),
                       FUN = sum, na.rm = TRUE)
colnames(noTargetSelected) <- c("Layout", "Method", "ProbID", "NoTargetSelected")
p3 <- data_summary_plot(
  data = noTargetSelected,
  varname = "NoTargetSelected",
  groupnames = c("Layout", "Method"),
  fun.error = se,
  p.scale_fill_manual = c("aquamarine", "bisque", "coral", "darkolivegreen"),
  p.basesize = 12
) +
  ggtitle("No targets selected") +
  theme(
    text = element_text(size = 16),
    plot.title = element_text(size = 16, hjust = 0.5),
    legend.title = element_blank(),
    axis.title.x = element_blank(),
    axis.title.y = element_blank()
  )

# Plot overshoots
data.overshoot <- data.raw %>% filter(Method != "Direct Touch")

overshoot <- aggregate(data.overshoot$Overshoot,
                       by = list(data.overshoot$Layout, data.overshoot$Method, data.overshoot$ProbID),
                       FUN = sum, na.rm = TRUE)
colnames(overshoot) <- c("Layout", "Method", "ProbID", "Overshoot")
p4 <- data_summary_plot(
  data = overshoot,
  varname = "Overshoot",
  groupnames = c("Layout", "Method"),
  fun.error = se,
  p.scale_fill_manual = c("aquamarine", "bisque", "darkolivegreen"),
  p.basesize = 12
) +
  ggtitle("Mean overshoot") +
  theme(
    text = element_text(size = 16),
    plot.title = element_text(size = 16, hjust = 0.5),
    legend.title = element_blank(),
    axis.title.x = element_blank(),
    axis.title.y = element_blank()
  )

# Plot overshoot time
overshootTime <- aggregate(data.overshoot$OvershootTime,
                       by = list(data.overshoot$Layout, data.overshoot$Method, data.overshoot$ProbID),
                       FUN = sum, na.rm = TRUE)
colnames(overshootTime) <- c("Layout", "Method", "ProbID", "Overshoot time")
p5 <- data_summary_plot(
  data = overshootTime,
  varname = "Overshoot time",
  groupnames = c("Layout", "Method"),
  fun.error = se,
  p.scale_fill_manual = c("aquamarine", "bisque", "darkolivegreen"),
  p.basesize = 12
) +
  ggtitle("Mean overshoot time") +
  theme(
    text = element_text(size = 16),
    plot.title = element_text(size = 16, hjust = 0.5),
    legend.title = element_blank(),
    axis.title.x = element_blank(),
    axis.title.y = element_blank()
  )

# Combine and save plots
data.plots1 <- grid_arrange_shared_legend(p1, p2, p3, nrow = 1, ncol = 3)
ggsave(file.path("experimental_1_3.pdf"), plot=data.plots1, width = 15, height = 7, limitsize = FALSE)
data.plots2 <- grid_arrange_shared_legend(p4, p5, nrow = 1, ncol = 2)
ggsave(file.path("experimental_4_5.pdf"), plot=data.plots2, width = 15, height = 7, limitsize = FALSE)


# calculate mean and standard deviation values
data.filtered = subset(data.raw, Method == "Direct Touch" & Layout == "Vertical")
mean(data.filtered$TaskCompletionTime)
sd(data.filtered$TaskCompletionTime)
mean(data.filtered$ErrorRate)
sd(data.filtered$ErrorRate)
mean(data.filtered$NoTargetSelected)
sd(data.filtered$NoTargetSelected)
mean(data.filtered$Overshoot)
sd(data.filtered$Overshoot)
mean(data.filtered$OvershootTime)
sd(data.filtered$OvershootTime)

data.filtered = subset(data.raw, Method == "Direct Touch")
mean(data.filtered$TaskCompletionTime)
sd(data.filtered$TaskCompletionTime)
data.filtered = subset(data.raw, Method == "Indirect Touch")
mean(data.filtered$TaskCompletionTime)
sd(data.filtered$TaskCompletionTime)
data.filtered = subset(data.raw, Method == "Analogue Stick")
mean(data.filtered$TaskCompletionTime)
sd(data.filtered$TaskCompletionTime)
data.filtered = subset(data.raw, Method == "Buttons")
mean(data.filtered$TaskCompletionTime)
sd(data.filtered$TaskCompletionTime)
