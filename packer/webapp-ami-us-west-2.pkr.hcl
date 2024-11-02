packer {
  required_plugins {
    amazon = {
      source  = "github.com/hashicorp/amazon"
      version = ">= 1.0.0,< 2.0.0"
    }
  }
}

variable "aws_region" {
  type    = string
  default = "us-west-2"
}

variable "source_ami" {
  type    = string
  default = "ami-04dd23e62ed049936" //Ubuntu24.04 LTS us-west-2
}

variable "ssh_username" {
  type    = string
  default = "ubuntu"
}

variable "subnet_id" {
  type    = string
  default = "subnet-0627479f71a0d731f" //us-west-2
}

variable "instance_type" {
  type    = string
  default = "t2.small"
}

variable "delay_seconds" {
  type    = number
  default = 120
}

variable "max_attempts" {
  type    = number
  default = 50
}

variable "device_name" {
  type    = string
  default = "/dev/sda1"
}

variable "volume_size" {
  type    = number
  default = 8
}

variable "volume_type" {
  type    = string
  default = "gp2"
}

variable "demo_user" {
  type    = string
  default = "977098991229"
}

source "amazon-ebs" "my-ami" {
  region          = "${var.aws_region}"
  ami_name        = "csye6225_f24_webapp_${formatdate("YYYY_MM_DD_HH_mm_ss", timestamp())}"
  ami_description = "AMI for CSYE6225"

  ami_regions = [
    "${var.aws_region}",
  ]

  ami_users = ["${var.demo_user}"]

  aws_polling {
    delay_seconds = var.delay_seconds
    max_attempts  = var.max_attempts
  }

  instance_type = "${var.instance_type}"
  source_ami    = "${var.source_ami}"
  ssh_username  = "${var.ssh_username}"
  subnet_id     = "${var.subnet_id}"

  launch_block_device_mappings {
    delete_on_termination = true
    device_name           = "${var.device_name}"
    volume_size           = var.volume_size
    volume_type           = var.volume_type
  }

  tags = {
    Name = "csye6225-webapp"
  }
}

build {
  sources = [
    "source.amazon-ebs.my-ami",
  ]


  provisioner "shell" {
    script = "updateOs.sh"
  }

  provisioner "shell" {
    script = "appDirSetup.sh"
  }

  provisioner "file" {
    source      = "../target/webapp-0.0.1-SNAPSHOT.jar"
    destination = "/tmp/webapp.jar"
  }


  provisioner "file" {
    source = "app.service"
    destination = "/tmp/app.service"
  }

  provisioner "file" {
    source = "cloundWatchAgentConfig.json"
    destination = "/tmp/cloundWatchAgentConfig.json"
  }

  provisioner "shell" {
    script = "dependency.sh"
  }


  provisioner "shell" {
    script = "appSetup.sh"
  }

  provisioner "shell" {
    script = "cloudWatchAgent.sh"
  }

}