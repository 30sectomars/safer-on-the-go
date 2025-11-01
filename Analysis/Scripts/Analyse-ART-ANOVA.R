library(ARTool)
library(emmeans)
library(tidyverse)
library(plyr)      
library(ggplot2)

data.raw <- read.delim("../all_experimental_merged.txt", header = T)

data.raw$ProbID <- factor(data.raw$ProbID)
data.raw$Method <- factor(data.raw$Method)
data.raw$Layout <- factor(data.raw$Layout)

data.filtered <- subset(data.raw, Method != "Direct Touch")

# ART ANOVA - Task completion time
model.taskCompletionTime <- art(TaskCompletionTime ~ Method * Layout + (1|ProbID), data= data.raw)
model.taskCompletionTime.anova <- anova(model.taskCompletionTime)
model.taskCompletionTime.anova$eta.sq.part = with(model.taskCompletionTime.anova, (F * Df) / (F * Df + Df.res))
print(model.taskCompletionTime.anova)

#contrast(emmeans(artlm(model.taskCompletionTime, "Method"), ~ Method), method = "pairwise")
art.con(model.taskCompletionTime, "Method")
art.con(model.taskCompletionTime, "Layout")
art.con(model.taskCompletionTime, "Method:Layout")

# ART ANOVA - Error rate
model.errorRate <- art(ErrorRate ~ Method * Layout + (1|ProbID), data= data.raw)
model.errorRate.anova <- anova(model.errorRate)
model.errorRate.anova$eta.sq.part = with(model.errorRate.anova, (F * Df) / (F * Df + Df.res))
print(model.errorRate.anova)

art.con(model.errorRate, "Method")
art.con(model.errorRate, "Layout")
art.con(model.errorRate, "Method:Layout")

#ART-Anova - No target selected
model.noTargetSelected <- art(NoTargetSelected ~ Method * Layout + (1|ProbID), data= data.raw)
model.noTargetSelected.anova <- anova(model.noTargetSelected)
model.noTargetSelected.anova$eta.sq.part = with(model.noTargetSelected.anova, (F * Df) / (F * Df + Df.res))
print(model.noTargetSelected.anova)

art.con(model.noTargetSelected, "Method")
art.con(model.noTargetSelected, "Layout")
art.con(model.noTargetSelected, "Method:Layout")

#ART-Anova - Overshoot
model.overshoot <- art(Overshoot ~ Method * Layout + (1|ProbID), data= data.filtered)
model.overshoot.anova <- anova(model.overshoot)
model.overshoot.anova$eta.sq.part = with(model.overshoot.anova, (F * Df) / (F * Df + Df.res))
print(model.overshoot.anova)

art.con(model.overshoot, "Method")
art.con(model.overshoot, "Layout")
art.con(model.overshoot, "Method:Layout")

#ART-Anova - Overshoot time
model.overshootTime <- art(OvershootTime ~ Method * Layout + (1|ProbID), data= data.filtered)
model.overshootTime.anova <- anova(model.overshootTime)
model.overshootTime.anova$eta.sq.part = with(model.overshootTime.anova, (F * Df) / (F * Df + Df.res))
print(model.overshootTime.anova)

art.con(model.overshootTime, "Method")
art.con(model.overshootTime, "Layout")
art.con(model.overshootTime, "Method:Layout")
