package com.ssafy.boney.domain.user.repository;

import com.ssafy.boney.domain.user.entity.ParentChild;
import com.ssafy.boney.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParentChildRepository extends JpaRepository<ParentChild, Integer> {

    // 보호자-아이 관계가 존재하는지 확인
    boolean existsByParentAndChild(User parent, User child);
    List<ParentChild> findByParent(User parent);
    Optional<ParentChild> findByParentUserIdAndChildUserId(Integer parentUserId, Integer childUserId);
    Optional<ParentChild> findByChild(User child);

}
