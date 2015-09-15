package com.gemstone.gemfire.distributed.internal.membership.gms.locator;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;

import com.gemstone.gemfire.DataSerializer;
import com.gemstone.gemfire.distributed.internal.DistributionManager;
import com.gemstone.gemfire.distributed.internal.HighPriorityDistributionMessage;
import com.gemstone.gemfire.distributed.internal.membership.InternalDistributedMember;
import com.gemstone.gemfire.internal.DataSerializableFixedID;
import com.gemstone.gemfire.internal.Version;

public class FindCoordinatorRequest implements DataSerializableFixedID, PeerLocatorRequest {

  private InternalDistributedMember memberID;
  private Collection<InternalDistributedMember> rejectedCoordinators;
  private int lastViewId;
  
  public FindCoordinatorRequest(InternalDistributedMember myId) {
    this.memberID = myId;
  }
  
  public FindCoordinatorRequest(InternalDistributedMember myId, Collection<InternalDistributedMember> rejectedCoordinators, int lastViewId) {
    this.memberID = myId;
    this.rejectedCoordinators = rejectedCoordinators;
    this.lastViewId = lastViewId;
  }
  
  public FindCoordinatorRequest() {
    // no-arg constructor for serialization
  }

  public InternalDistributedMember getMemberID() {
    return memberID;
  }
  
  public Collection<InternalDistributedMember> getRejectedCoordinators() {
    return rejectedCoordinators;
  }
  
  public int getLastViewId() {
    return this.lastViewId;
  }
  
  @Override
  public String toString() {
    if (rejectedCoordinators != null) {
      return "FindCoordinatorRequest(memberID="+memberID
          +", rejected="+rejectedCoordinators+", lastViewId="+lastViewId+")";
    } else {
      return "FindCoordinatorRequest(memberID="+memberID+")";
    }
  }

  @Override
  public Version[] getSerializationVersions() {
    return null;
  }

  @Override
  public int getDSFID() {
    return FIND_COORDINATOR_REQ;
  }

  @Override
  public void toData(DataOutput out) throws IOException {
    DataSerializer.writeObject(this.memberID, out);
    DataSerializer.writeObject(this.rejectedCoordinators, out);
    out.writeInt(lastViewId);
  }

  @Override
  public void fromData(DataInput in) throws IOException, ClassNotFoundException {
    this.memberID = DataSerializer.readObject(in);
    this.rejectedCoordinators = DataSerializer.readObject(in);
    this.lastViewId = in.readInt();
  }

}
