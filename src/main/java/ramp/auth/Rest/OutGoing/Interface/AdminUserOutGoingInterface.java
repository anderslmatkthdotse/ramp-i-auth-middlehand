package ramp.auth.Rest.OutGoing.Interface;

import ramp.auth.Rest.DTO.UserDTO;

import java.util.List;

public interface AdminUserOutGoingInterface extends UserOutGoingInterface{

    List<UserDTO> getUsers();
}
