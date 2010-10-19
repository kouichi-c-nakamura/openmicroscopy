#!/bin/bash
# install VM from snapshot

export VMNAME=${VMNAME:-"$1"}
export VMNAME=${VMNAME:-"OMERO42"}

export MEMORY=${MEMORY:-"1024"}
export SSH_PF=${SSH_PF:-"2222"}
export OMERO_PORT=${OMERO_PORT:-"4063"}
export OMERO_PF=${OMERO_PF:-"4063"}
export OMEROS_PORT=${OMEROS_PORT:-"4064"}
export OMEROS_PF=${OMEROS_PF:-"4064"}

export OMEROWEB_PORT=${OMEROWEB_PORT:-"80"}
export OMEROWEB_PF=${OMEROWEB_PF:-"8080"}
export HARDDISKS=${HARDDISKS:-"$HOME/Library/VirtualBox/HardDisks/"}
export VBDISKS=${VBDISKS:-"$HOME/Library/VirtualBox/"}

#export HARDDISKS=${HARDDISKS:-"$HOME/.VirtualBox/HardDisks/"}
export SRCVDI=${SRCVDI:-"$HOME/Dev/VM/OMERO-SSH.vdi"}

set -e
set -u
set -x

#prepare space
rm -rf "$VBDISKS"*
mkdir "$HARDDISKS"
echo "Coping $HARDDISKS"OMERO-SSH.vdi"..."
cp "$SRCVDI" "$HARDDISKS"OMERO-SSH.vdi""

VBoxManage clonehd "$HARDDISKS"OMERO-SSH.vdi"" "$HARDDISKS$VMNAME.vdi"
VBoxManage createvm --name "$VMNAME" --register --ostype "Ubuntu_64"
VBoxManage storagectl "$VMNAME" --name "IDE CONTROLLER" --add ide
VBoxManage storagectl "$VMNAME" --name "SATA CONTROLLER" --add sata
VBoxManage storageattach "$VMNAME" --storagectl "SATA CONTROLLER" --port 0 --device 0 --type hdd --medium $HARDDISKS$VMNAME.vdi
# VBoxManage storageattach "$VMNAME" --storagectl "IDE CONTROLLER" --device 0 --port 1 --type dvddrive --medium ~/Desktop/linux\ distros/ubuntu\ 10.04/ubuntu-10.04-server-amd64.iso
VBoxManage modifyvm "$VMNAME" --nic1 nat --nictype1 "Am79C973" --macaddress1 080027FBF54E
VBoxManage modifyvm "$VMNAME" --memory $MEMORY --acpi on
VBoxManage setextradata "$VMNAME" "VBoxInternal/Devices/pcnet/0/LUN#0/Config/ssh/HostPort" $SSH_PF
VBoxManage setextradata "$VMNAME" "VBoxInternal/Devices/pcnet/0/LUN#0/Config/ssh/GuestPort" 22
VBoxManage setextradata "$VMNAME" "VBoxInternal/Devices/pcnet/0/LUN#0/Config/ssh/Protocol" TCP
VBoxManage setextradata "$VMNAME" "VBoxInternal/Devices/pcnet/0/LUN#0/Config/omeroserver/HostPort" $OMERO_PF
VBoxManage setextradata "$VMNAME" "VBoxInternal/Devices/pcnet/0/LUN#0/Config/omeroserver/GuestPort" $OMERO_PORT
VBoxManage setextradata "$VMNAME" "VBoxInternal/Devices/pcnet/0/LUN#0/Config/omeroservers/Protocol" TCP
VBoxManage setextradata "$VMNAME" "VBoxInternal/Devices/pcnet/0/LUN#0/Config/omeroservers/HostPort" $OMEROS_PF
VBoxManage setextradata "$VMNAME" "VBoxInternal/Devices/pcnet/0/LUN#0/Config/omeroservers/GuestPort" $OMEROS_PORT
VBoxManage setextradata "$VMNAME" "VBoxInternal/Devices/pcnet/0/LUN#0/Config/omeroserver/Protocol" TCP
VBoxManage setextradata "$VMNAME" "VBoxInternal/Devices/pcnet/0/LUN#0/Config/omeroweb/HostPort" $OMEROWEB_PF
VBoxManage setextradata "$VMNAME" "VBoxInternal/Devices/pcnet/0/LUN#0/Config/omeroweb/GuestPort" $OMEROWEB_PORT
VBoxManage setextradata "$VMNAME" "VBoxInternal/Devices/pcnet/0/LUN#0/Config/omeroweb/Protocol" TCP

#VBoxManage getextradata $VMNAME enumerate