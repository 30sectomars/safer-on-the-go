#+++++++++++++++++++++++++
# Function to calculate the mean and the standard deviation
# for each group
#+++++++++++++++++++++++++
# data : a data frame
# varname : the name of a column containing the variable
#to be summariezed
# groupnames : vector of column names to be used as
# grouping variables
data_summary <- function(data, varname, groupnames, fun.error = sd){
  require(dplyr)
  summary_func <- function(x, col){
    c(mean = mean(x[[col]], na.rm=TRUE),
      #sd = ifelse(is.logical(x[[col]]), NA, sd(x[[col]], na.rm=TRUE)))
      error = fun.error(x[[col]], na.rm=TRUE))
  }
  data_sum<-ddply(data, groupnames, .fun=summary_func,
                  varname)
  #data_sum <- rename(data_sum, c("mean" = varname))
  return(data_sum)
}

se <- function(x, na.rm=FALSE) {
  #return(sd(x, na.rm=na.rm)/sqrt(length(x)))
  return(sd(x, na.rm=na.rm)/sqrt(ifelse(na.rm, length(x[!is.na(x)]), length(x))))
}

data_summary_plot_multiple <- function(data, varnames, groupnames, outdir, prefix, p.width, p.height, fun.error = sd,  scale_fill_manual = NULL) {
  
  groupingString <- paste("_by", paste(groupnames, collapse="_"), sep="_")
  
  for(varname in varnames) {
    p <- data_summary_plot(data, varname, groupnames, fun.error, scale_fill_manual)
    filename = paste(prefix, "_", varname, groupingString, ".pdf", sep="")
    outPath = file.path(outdir, filename)
    print(outPath)
    if(any(missing(p.width) | missing(p.height)))
      ggsave(outPath, plot=p, device = cairo_pdf)
    else
      ggsave(outPath, plot=p, device = cairo_pdf, width = p.width, height = p.height)
  }
}

data_summary_plot <- function(data, varname, groupnames, fun.error = sd, p.scale_fill_manual = NULL, p.basesize = 10){

  if(any(length(groupnames) < 1 | length(groupnames) > 3) )
    stop('Sorry, only 1-3 grouping variables supported')
  
  require(ggplot2)
  data.plot <- data_summary(data, varname, groupnames, fun.error)
  
  p<- NULL
  
  if(length(groupnames) == 1)
    p<- ggplot(data.plot, aes_string(x=groupnames[1], y="mean", fill=groupnames[1]))
  else
    p<- ggplot(data.plot, aes_string(x=groupnames[1], y="mean", fill=groupnames[2]))
  
  if(!is.null(p.scale_fill_manual))
    p <- p + scale_fill_manual(values = p.scale_fill_manual)
  
  p <- p + geom_bar(stat="identity", color="black", 
               position=position_dodge()) +
    geom_errorbar(aes(ymin=mean-error, ymax=mean+error), width=.2,
               position=position_dodge(.9))  +
    ylab(varname)

  
  if(length(groupnames) == 3) {
    p <- p + facet_wrap(as.formula(paste("~", groupnames[3])))
  }
  
  p <- p + theme_minimal(base_size = p.basesize)
  
  return(p)
}

reshape_to_wide <- function(data, IVs, groupnames, ParticipantColName){
  require(data.table)

  data.wide <- data.table::dcast(setDT(data), formula = as.formula(paste(ParticipantColName, "~",  paste(groupnames, collapse="+"))), value.var = IVs, fun.aggregate = mean, na.rm = TRUE)

  return(data.wide)
}

rbind_create <- function(data, row) {
  if(is.null(data))
    data <- row
  else
    data <- rbind(data, row)
  return(data)
}