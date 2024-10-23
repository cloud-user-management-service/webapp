packer {
  required_plugins {
    amazon = {
      source  = "github.com/hashicorp/amazon"
      version = ">= 1.0.0,< 2.0.0"
    }
  }
}


variable "source_ami" {
  type = string
  // default = "ami-0c55b159cbfafe1f0" //Ubuntu24.04 LTS us-east-1
  default = "ami-04dd23e62ed049936" //Ubuntu24.04 LTS us-west-2
}

variable "ssh_username" {
  type    = string
  default = "ubuntu"
}

variable "subnet_id" {
  type    = string
  default = "subnet-04e9c1c2a8f0a6014" //us-west-2
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

variable "mysql_root_password" {
  type    = string
  default = "mysql"
}

variable "mysql_db_name" {
  type    = string
  default = "webapp"
}

variable "mysql_url" {
  type    = string
  default = "jdbc:mysql://localhost:3306/webapp"
}

variable "mysql_user_name" {
  type    = string
  default = "root"
}

variable "mysql_password" {
  type    = string
  default = "mysql"
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

// build {
//     sources = [
//         "source.amazon-ebs.my-ami",
//     ]

//     provisioner "shell" {
//         environment_vars = [
//             "DEBIAN_FRONTEND=noninteractive"
//             "CHECKPOINT_DISABLE=1"
//         ]

//         inline = [
//             "sudo apt-get update",
//             "sudo apt-get upgrade -y",
//             "sudo apt-get install nginx -y",
//             "sudo apt-get clean",
//         ]
//     }
// }

build {
  sources = [
    "source.amazon-ebs.my-ami",
  ]

  // #create csye6225 user with no login
  // provisioner "shell" {
  //     script = "userSetup.sh"
  // }

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

  // provisioner "file" {
  //     source      = "app.properties"
  //     destination = "/opt/myapp/app.properties"
  //     // destination = "/tmp/app.properties"
  // }

  // provisioner "file" {
  //     source      = "app"
  //     destination = "/tmp/app"
  // }

  provisioner "file" {
    source = "app.service"
    // destination = "/etc/systemd/system/csye6225.service"
    destination = "/tmp/app.service"
  }

  provisioner "shell" {
    environment_vars = [
      "MYSQL_ROOT_PASSWORD=${var.mysql_root_password}",
      "DB_NAME=${var.mysql_db_name}",
      "DB_URL=${var.mysql_url}",
      "DB_USERNAME=${var.mysql_user_name}",
      "DB_PASSWORD=${var.mysql_password}"
    ]
    script = "dependency.sh"
  }

  // provisioner "file" {
  //     source = "target/webapp.jar" //??
  //     destination = "/tmp/webapp.jar"//??
  // }

  provisioner "shell" {
    script = "appSetup.sh"
  }

}