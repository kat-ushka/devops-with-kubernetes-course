# Exercise 3.06: DBaaS vs DIY

<!-- TOC -->
* [Exercise description](#exercise-description)
* [Exercise realization](#exercise-realization)
  * [DBaaS](#dbaas)
    * [Pricing](#pricing)
    * [Pros](#pros)
    * [Cons](#cons)
  * [DIY](#diy)
    * [Pricing](#pricing)
    * [Pros](#pros)
    * [Cons](#cons)
  * [Conclusion](#conclusion)
<!-- TOC -->

## Exercise description

Do a pros/cons comparison of the solutions in terms of meaningful differences. This includes at least the required work and cost to initialize as well as maintain. Backup methods and their ease of usage should be considered as well.

Set the list into the README of the project.

## Exercise realization

Let's try to approximately calculate pricing for both approaches on the example of the next configuration we request for a year:
PostgeSQL database in europe-north1 zone with configuration of 4 vCPU, 16Gb Memory and expect storage growth of 5 Gb (SSD) per month.

Let's assume also that we do not need high availability, we store 2 last backups, and all the networking is within the same region for both of that configurations.

### DBaaS

#### Pricing

Let's try to calculate the price for a year using 1-year commitment price:

1. 4 vCPU will cost $24.8711 * 4 (vCPU) * 12 (month) ~= $1194
2. 16 Gb will cost $4.2194 * 16 (Gb) * 12 (month) ~= $810
3. Storage: $0.187 * ((5 + (5 + 5 * 11)) * 12) / 2 = $0.187 * 390 ~= $73
4. Backups: $0.088 * ((10 + (10 + 10 * 11)) * 12) / 2 = $0.088 * 780 ~= $69

Total is $2146.

#### Pros

1. No need for database administrator skills or a separate person with such a skillset.
2. No time spends on setting up environments, network, security. You can just start.
3. There is no need in dealing with backups (the schedule, the storage, the monitoring).
4. Software updates and other maintaining is covered by the service.
5. You don't have to worry about storage capacity and predict its growth. It will be increased automatically if needed.
6. Replication is covered by the service if required.

#### Cons

1. It costs more than a DIY approach. In our example it is almost 50% more.
2. The pricing is tricky, and you have to monitor used resourced not to get surprisingly big bill.

### DIY

#### Pricing

Let's assume we are putting our database in a VM in a GCP:

1. VM e2-standard-4 (4 vCPU, 16 Gb Memory) with 60 Gb SSD disk will cost $120.04 * 12 ~= $1440
2. Cloud storage Nearline bucket for backups will cost $0.010 * ((10 + (10 + 10 * 11)) * 12) / 2 = $0.004 * 780 = $8
3. Retrieving backups (assume twice a year when backup is 30 Gb and 60 Gb) will cost $0.01 * 30 + $0.01 * 60 ~= $1

Total is $1449.

#### Pros

1. It costs less than a DBaaS approach. In our example it is almost 50% less.
2. You have more control on what is going on.

#### Cons

1. You have to be familiar with the database you use or have a dedicated person who is.
2. You have to spend much time (I would assume at least 2 man-days per stage) on configuring VM and DB for every stage you need.
3. You have to maintain the VM and the DB: plan, test and make updates, backups and so on.
4. You have to monitor and predict capacity growth, so you wouldn't run out of space for the DB and/or backups.

### Conclusion

I would say that you would prefer to have DIY database if:

1. You have database administrator hours you need to utilize OR you have enough skills to do it by yourself.
2. You need full control on the data transferring and security (in that case you may even use an owned server and not a cloud).
3. Your project is quite small or really large. In that case the extra expenses are significant. 

And you'd prefer to use DBaaS if:

1. You can afford spending extra money.
2. You are starting a new project and speed is a key advantage for you.
3. You do not have dba skills and would prefer not to pay for a dedicated person for them.