library(ARTool)
library(emmeans)
library(tidyverse)
library(plyr)      
library(ggplot2)
source("HelperScripts/summary.r")
source("HelperScripts/grid_arrange_shared_legend.r")

data.raw <- read.delim("../all_eye_tracker.txt", header = T)

data.raw$ProbID <- factor(data.raw$ProbID)
data.raw$Method <- factor(data.raw$Method)

data.raw$Gaze_Factor <- data.raw$Gaze_Points_Tablet / data.raw$Total_Gaze_Points
data.raw$Frame_Factor <- data.raw$Tablet / data.raw$Frame_Count

data.DT = subset(data.raw, Method == "Direct Touch")
data.IT = subset(data.raw, Method == "Indirect Touch")
data.AS = subset(data.raw, Method == "Analogue Stick")
data.BT = subset(data.raw, Method == "Buttons")

shapiro_tests_gaze <- c(shapiro.test(as.numeric(data.DT$Gaze_Points_Tablet))$p.value,
                         shapiro.test(as.numeric(data.IT$Gaze_Points_Tablet))$p.value,
                         shapiro.test(as.numeric(data.AS$Gaze_Points_Tablet))$p.value,
                         shapiro.test(as.numeric(data.BT$Gaze_Points_Tablet))$p.value
)

alpha_gaze <- keep(shapiro_tests_gaze, function(x) x > 0.05)
alpha_gaze_count <- length(alpha_gaze)

shapiro_tests_frame <- c(shapiro.test(as.numeric(data.DT$Tablet))$p.value,
                         shapiro.test(as.numeric(data.IT$Tablet))$p.value,
                         shapiro.test(as.numeric(data.AS$Tablet))$p.value,
                         shapiro.test(as.numeric(data.BT$Tablet))$p.value
)

alpha_frame <- keep(shapiro_tests_frame, function(x) x > 0.05)
alpha_frame_count <- length(alpha_frame)

# ART ANOVA - Eye Tracker - Gaze factor
model.gazeFactor <- art(Gaze_Factor ~ Method + (1|ProbID), data= data.raw)
model.gazeFactor.anova <- anova(model.gazeFactor)
model.gazeFactor.anova$eta.sq.part = with(model.gazeFactor.anova, (F * Df) / (F * Df + Df.res))
print(model.gazeFactor.anova)

art.con(model.gazeFactor, "Method")

# ART ANOVA - Eye Tracker - Frame factor
model.frameFactor <- art(Frame_Factor ~ Method + (1|ProbID), data= data.raw)
model.frameFactor.anova <- anova(model.frameFactor)
model.frameFactor.anova$eta.sq.part = with(model.frameFactor.anova, (F * Df) / (F * Df + Df.res))
print(model.frameFactor.anova)

art.con(model.frameFactor, "Method")

# Plot gaze distribution
gazeDistribution <- aggregate(data.raw$Gaze_Factor,
                              by = list(data.raw$Method, data.raw$ProbID),
                              FUN = sum, na.rm = TRUE)
colnames(gazeDistribution) <- c("Method", "ProbID", "GazeDistribution")
p1 <- data_summary_plot(
  data = gazeDistribution,
  varname = "GazeDistribution",
  groupnames = c("Method"),
  fun.error = se,
  p.scale_fill_manual = c("aquamarine", "bisque", "coral", "darkolivegreen"),
  p.basesize = 12
) +
  ggtitle("Gaze distribution") +
  theme(
    text = element_text(size = 16),
    plot.title = element_text(size = 16, hjust = 0.5),
    legend.title = element_blank(),
    axis.title.x = element_blank(),
    axis.title.y = element_blank()
  )

# Plot frame distribution
frameDistribution <- aggregate(data.raw$Frame_Factor,
                              by = list(data.raw$Method, data.raw$ProbID),
                              FUN = sum, na.rm = TRUE)
colnames(frameDistribution) <- c("Method", "ProbID", "FrameDistribution")
p2 <- data_summary_plot(
  data = frameDistribution,
  varname = "FrameDistribution",
  groupnames = c("Method"),
  fun.error = se,
  p.scale_fill_manual = c("aquamarine", "bisque", "coral", "darkolivegreen"),
  p.basesize = 12
) +
  ggtitle("Frame distribution") +
  theme(
    text = element_text(size = 16),
    plot.title = element_text(size = 16, hjust = 0.5),
    legend.title = element_blank(),
    axis.title.x = element_blank(),
    axis.title.y = element_blank()
  )

data.plots2 <- grid_arrange_shared_legend(p1, p2, nrow = 1, ncol = 2)
ggsave(file.path("eye_tracker.pdf"), plot=data.plots2, width = 15, height = 7, limitsize = FALSE)

data.filtered = subset(data.raw, Method == "Direct Touch")
mean(data.filtered$Gaze_Factor)
mean(data.filtered$Frame_Factor)
sd(data.filtered$Gaze_Factor)
sd(data.filtered$Frame_Factor)
data.filtered = subset(data.raw, Method == "Indirect Touch")
mean(data.filtered$Gaze_Factor)
mean(data.filtered$Frame_Factor)
sd(data.filtered$Gaze_Factor)
sd(data.filtered$Frame_Factor)
data.filtered = subset(data.raw, Method == "Analogue Stick")
mean(data.filtered$Gaze_Factor)
mean(data.filtered$Frame_Factor)
sd(data.filtered$Gaze_Factor)
sd(data.filtered$Frame_Factor)
data.filtered = subset(data.raw, Method == "Buttons")
mean(data.filtered$Gaze_Factor)
mean(data.filtered$Frame_Factor)
sd(data.filtered$Gaze_Factor)
sd(data.filtered$Frame_Factor)
