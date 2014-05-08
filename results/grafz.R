library(plyr)
library(ggplot2)
library(foreach)
library(gridExtra)

path="/home/pv/Documents/CS645-DatabassProject/results"

data.iris <- read.table(paste(path, 'time', sep='/'), header=F)
data.social <- read.table(paste(path, 'social-time', sep='/'), header=F)

data <- rbind(data.iris, data.social)
colnames(data) <- c('system', 'time', 'query', 'data.size')

data <- ddply(data, cbind("system","query","data.size"), numcolwise(mean))
data$data.size <- data$data.size*1000

q2.plot <- ggplot(subset(data, query=='2'), aes(x=data.size, y=time, fill=as.factor(system) )) + geom_bar(position="dodge", stat="identity") + labs(title='Query 2 Test Set Run Times', x='Data Set Size', y='Time (ms)', fill='System')  + scale_x_discrete(limits=c(1000, 10000), breaks=c(1000, 10000))

print(q2.plot)

q3.plot <- ggplot(subset(data, query=='3'), aes(x=data.size, y=time, fill=as.factor(system) )) + geom_bar(position="dodge", stat="identity") + labs(title='Query 3 Test Set Run Times', x='Data Set Size', y='Time (ms)', fill='System') + scale_x_discrete(limits=c(1000, 10000), breaks=c(1000, 10000))

print(q3.plot)

ggsave(paste(path, 'query2.pdf', sep="/"), q2.plot, width = 8, height = 5)
ggsave(paste(path, 'query3.pdf', sep="/"), q3.plot, width = 8, height = 5)
