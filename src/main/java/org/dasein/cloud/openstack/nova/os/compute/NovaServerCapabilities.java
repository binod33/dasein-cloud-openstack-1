/**
 * Copyright (C) 2009-2015 Dell, Inc.
 * See annotations for authorship information
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.dasein.cloud.openstack.nova.os.compute;

import org.dasein.cloud.*;
import org.dasein.cloud.compute.Architecture;
import org.dasein.cloud.compute.ImageClass;
import org.dasein.cloud.compute.Platform;
import org.dasein.cloud.compute.VMScalingCapabilities;
import org.dasein.cloud.compute.VirtualMachineCapabilities;
import org.dasein.cloud.compute.VmState;
import org.dasein.cloud.identity.IdentityServices;
import org.dasein.cloud.identity.ShellKeySupport;
import org.dasein.cloud.network.NetworkServices;
import org.dasein.cloud.network.VLANSupport;
import org.dasein.cloud.openstack.nova.os.NovaOpenStack;
import org.dasein.cloud.util.NamingConstraints;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

/**
 * Describes the capabilities of Openstack with respect to Dasein virtual machine operations.
 * <p>Created by Danielle Mayne: 3/03/14 12:51 PM</p>
 * @author Danielle Mayne
 * @version 2014.03 initial version
 * @since 2014.03
 */

public class NovaServerCapabilities extends AbstractCapabilities<NovaOpenStack> implements VirtualMachineCapabilities {


    public NovaServerCapabilities(@Nonnull NovaOpenStack cloud) { super(cloud); }

    @Override
    public boolean canAlter(@Nonnull VmState fromState) throws CloudException, InternalException {
        return VmState.RUNNING.equals(fromState) || VmState.STOPPED.equals(fromState);
    }

    @Override
    public boolean canClone(@Nonnull VmState fromState) throws CloudException, InternalException {
        return false;
    }

    @Override
    public boolean canPause(@Nonnull VmState fromState) throws CloudException, InternalException {
        boolean canPause = ((NovaOpenStack)getProvider()).getCloudProvider().supportsPauseUnpause(null);
        if (canPause) {
            return !fromState.equals(VmState.PAUSED) && !fromState.equals(VmState.ERROR);
        }
        return canPause;
    }

    @Override
    public boolean canReboot(@Nonnull VmState fromState) throws CloudException, InternalException {
        return !fromState.equals(VmState.ERROR);
    }

    @Override
    public boolean canResume(@Nonnull VmState fromState) throws CloudException, InternalException {
        boolean canResume = ((NovaOpenStack)getProvider()).getCloudProvider().supportsSuspendResume(null);
        if (canResume) {
            return !fromState.equals(VmState.RUNNING) && !fromState.equals(VmState.ERROR);
        }
        return canResume;
    }

    @Override
    public boolean canStart(@Nonnull VmState fromState) throws CloudException, InternalException {
        boolean canStart = ((NovaOpenStack)getProvider()).getCloudProvider().supportsStartStop(null);
        if (canStart) {
            return !fromState.equals(VmState.RUNNING) && !fromState.equals(VmState.ERROR);
        }
        return canStart;
    }

    @Override
    public boolean canStop(@Nonnull VmState fromState) throws CloudException, InternalException {
        boolean canStop = ((NovaOpenStack)getProvider()).getCloudProvider().supportsStartStop(null);
        if (canStop) {
            return !fromState.equals(VmState.STOPPED) && !fromState.equals(VmState.ERROR);
        }
        return canStop;
    }

    @Override
    public boolean canSuspend(@Nonnull VmState fromState) throws CloudException, InternalException {
        boolean canSuspend = ((NovaOpenStack)getProvider()).getCloudProvider().supportsSuspendResume(null);
        if (canSuspend) {
            return !fromState.equals(VmState.SUSPENDED) && !fromState.equals(VmState.ERROR);
        }
        return canSuspend;
    }

    @Override
    public boolean canTerminate(@Nonnull VmState fromState) throws CloudException, InternalException {
        return !fromState.equals(VmState.TERMINATED);
    }

    @Override
    public boolean canUnpause(@Nonnull VmState fromState) throws CloudException, InternalException {
        boolean canUnpause = ((NovaOpenStack)getProvider()).getCloudProvider().supportsPauseUnpause(null);
        if (canUnpause) {
            return !fromState.equals(VmState.RUNNING) && !fromState.equals(VmState.ERROR);
        }
        return canUnpause;
    }

    @Override
    public int getMaximumVirtualMachineCount() throws CloudException, InternalException {
        return Capabilities.LIMIT_UNKNOWN;
    }

    @Override
    public int getCostFactor(@Nonnull VmState state) throws CloudException, InternalException {
        return 100;
    }

    @Nonnull
    @Override
    public String getProviderTermForVirtualMachine(@Nonnull Locale locale) throws CloudException, InternalException {
        return "server";
    }

    @Nullable
    @Override
    public VMScalingCapabilities getVerticalScalingCapabilities() throws CloudException, InternalException {
        return VMScalingCapabilities.getInstance(false, true, false);
    }

    @Nonnull
    @Override
    public NamingConstraints getVirtualMachineNamingConstraints() throws CloudException, InternalException {
        return NamingConstraints.getAlphaNumeric(1, 100);
    }

    @Nullable
    @Override
    public VisibleScope getVirtualMachineVisibleScope() {
        return VisibleScope.ACCOUNT_DATACENTER;
    }

    @Nullable
    @Override
    public VisibleScope getVirtualMachineProductVisibleScope() {
        return VisibleScope.ACCOUNT_DATACENTER;
    }

    @Nonnull
    @Override
    public Requirement identifyDataCenterLaunchRequirement() throws CloudException, InternalException {
        return Requirement.NONE;
    }

    @Nonnull
    @Override
    public Requirement identifyImageRequirement(@Nonnull ImageClass cls) throws CloudException, InternalException {
        return (cls.equals(ImageClass.MACHINE) ? Requirement.REQUIRED : Requirement.OPTIONAL);
    }

    @Nonnull
    @Override
    public Requirement identifyPasswordRequirement(Platform platform) throws CloudException, InternalException {
        return Requirement.NONE;
    }

    @Nonnull
    @Override
    public Requirement identifyRootVolumeRequirement() throws CloudException, InternalException {
        return Requirement.NONE;
    }

    @Nonnull
    @Override
    public Requirement identifyShellKeyRequirement(Platform platform) throws CloudException, InternalException {
        IdentityServices services = getProvider().getIdentityServices();

        if( services == null ) {
            return Requirement.NONE;
        }
        ShellKeySupport support = services.getShellKeySupport();
        if( support == null ) {
            return Requirement.NONE;
        }
        return Requirement.OPTIONAL;
    }

    @Nonnull
    @Override
    public Requirement identifyStaticIPRequirement() throws CloudException, InternalException {
        return Requirement.NONE;
    }

    @Nonnull
    @Override
    public Requirement identifySubnetRequirement() throws CloudException, InternalException {
        return Requirement.REQUIRED;
    }

    @Nonnull
    @Override
    public Requirement identifyVlanRequirement() throws CloudException, InternalException {
        NetworkServices services = getProvider().getNetworkServices();

        if( services == null ) {
            return Requirement.NONE;
        }
        VLANSupport support = services.getVlanSupport();

        if( support == null || !support.isSubscribed() ) {
            return Requirement.NONE;
        }
        return Requirement.REQUIRED;
    }

    @Override
    public boolean isAPITerminationPreventable() throws CloudException, InternalException {
        return false;
    }

    @Override
    public boolean isBasicAnalyticsSupported() throws CloudException, InternalException {
        return true;
    }

    @Override
    public boolean isExtendedAnalyticsSupported() throws CloudException, InternalException {
        return false;
    }

    @Override
    public boolean isUserDataSupported() throws CloudException, InternalException {
        return true; // you can only set userdata in OS, but you can't read.
    }

    @Override
    public boolean isUserDefinedPrivateIPSupported() throws CloudException, InternalException {
        return false;    //todo is supported in openstack but not in dasein yet
    }

    private transient Collection<Architecture> architectures;
    @Nonnull
    @Override
    public Iterable<Architecture> listSupportedArchitectures() throws InternalException, CloudException {
        if( architectures == null ) {
            ArrayList<Architecture> a = new ArrayList<Architecture>();

            a.add(Architecture.I32);
            a.add(Architecture.I64);
            architectures = Collections.unmodifiableList(a);
        }
        return architectures;
    }

    @Override
    public boolean supportsSpotVirtualMachines() throws InternalException, CloudException {
        return false;
    }

    @Override
    public boolean supportsClientRequestToken() throws InternalException, CloudException {
        return false;
    }

    @Override
    public boolean supportsCloudStoredShellKey() throws InternalException, CloudException{
        return false;
    }

    @Override
    public boolean isVMProductDCConstrained() throws InternalException, CloudException{
        return false;
    }

    @Override
    public boolean supportsAlterVM() {
        return true;
    }

    @Override
    public boolean supportsClone() {
        return false;
    }

    @Override
    public boolean supportsPause() {
        return ((NovaOpenStack)getProvider()).getCloudProvider().supportsPauseUnpause(null);
    }

    @Override
    public boolean supportsReboot() {
        return true;
    }

    @Override
    public boolean supportsResume() {
        return ((NovaOpenStack)getProvider()).getCloudProvider().supportsSuspendResume(null);
    }

    @Override
    public boolean supportsStart() {
        return ((NovaOpenStack)getProvider()).getCloudProvider().supportsStartStop(null);
    }

    @Override
    public boolean supportsStop() {
        return ((NovaOpenStack)getProvider()).getCloudProvider().supportsStartStop(null);
    }

    @Override
    public boolean supportsSuspend() {
        return ((NovaOpenStack)getProvider()).getCloudProvider().supportsSuspendResume(null);
    }

    @Override
    public boolean supportsTerminate() {
        return true;
    }

    @Override
    public boolean supportsUnPause() {
        return ((NovaOpenStack)getProvider()).getCloudProvider().supportsPauseUnpause(null);
    }
}
