# data "aws_vpc" "vpc_name" {
#   filter {
#     name   = "tag:Name"
#     values = [local.vpc_name]
#   }
# }

# data "aws_security_group" "fms_managed_sg" {
#   filter {
#     name   = "tag:Name"
#     values = [local.security_group_name]
#   }

#   filter {
#     name   = "vpc-id"
#     values = [data.aws_vpc.vpc_name.id]
#   }
# }

# # Get all subnet IDs within the specified VPC
# data "aws_subnets" "vpc_subnets" {
#   filter {
#     name   = "vpc-id"
#     values = [data.aws_vpc.vpc_name.id]
#   }

#   # If there are more 2 or more sets of Subnets under 1 VPC
#   filter {
#     name   = "tag:Name"
#     values = ["${local.subnet_names_prefix}*"]
#   }
# }