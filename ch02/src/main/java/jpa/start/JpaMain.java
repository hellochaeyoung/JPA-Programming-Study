package jpa.start;

import javax.persistence.*;
import java.util.List;

public class JpaMain {

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpaProject"); // 엔티티 매니저를 생성하는 공장이라 생각하자

        EntityManager em = emf.createEntityManager(); // 데이터베이스 CRUD 작업이 가능한 엔티티 매니저를 생성!!

        EntityTransaction tx = em.getTransaction(); // 트랜잭션 API를 받아옴, jpa는 무조건 트랜잭션 안에서 데이터를 변경해야하기 때문에!

        try {

            tx.begin();
            logic(em);
            tx.commit(); // 커밋하는 즉시 데이터베이스에 저장, 영속성 컨텍스트 내 쓰기 지연 SQL 저장소에 모아뒀던 INSERT SQL문들을 한번에!! => 이것이 Flush!!
            // Flush 호출될 때 엔티티와 스냅샷(영속성 컨텍스트에 보관 시 최초 상태를 복사해 저장해둔 것) 비교해 변경된 엔티티 찾아 수정 쿼리 작성해 쓰기 지연 SQL 저장소에 보냄
            // 트랜잭션 커밋 전 꼭 플러시를 호출해 영속성 컨텍스트의 변경 내용을 데이터베이스에 반영해야함!( 안 그러면 커밋해도 변경 사항이 없기 때문에 반영 안됨)
            // 따라서 커밋 시 Flush를 자동 호출 하는 것이당

        }catch (Exception e) {
            tx.rollback();
        }finally {
            em.close(); // 무조건 종료시켜줘야함
        }
        emf.close(); // 여기도 마찬가지!!
    }

    private static void logic(EntityManager em) {

        String id = "id1";
        Member member = new Member();
        member.setId(id);
        member.setUsername("채영");
        member.setAge(20);
        //여기까지 member는 비영속 상태이다. 단지 객체!!!

        em.persist(member); // 저장 함수, 이로써 영속 상태가 됨(비영속-> 영속), 엔티티 매니저의 영속성 컨텍스트에 의해 관리되는 상태!!
        // 영속성 컨텍스트 내부의 1차 캐시에 저장됨 -> 내부에 존재하는 Map에 저장되는 것!
        // 아직까지는 영속성 컨텍스트의 1차 캐시에만 저장되었다.
        // 이와 동시에 등록 쿼리를 쓰기 지연 SQL 저장소에 보관


        member.setAge(25); // JPA는 값이 변경되었는지 추적하는 기능을 갖고있음!! 따라서 값만 변경해주면 JPA가 알아서 UPDATE문을 작성해 반영한다 => 이것이 바로 변경 감지!!


        Member findMember = em.find(Member.class, id); // 1. 먼저 영속성 컨텍스트의 1차 캐시에서 찾고 2. 없으면 데이터베이스에서 조회(데이터베이스에서 가져오면 1차 캐시에 저장한 후 엔티티를 반환) => 이렇게 되면 성능 면에서 좋다!
        Member findMember2 = em.find(Member.class, id); // 식별자 같은 엔티티를 조회할 경우 1차 캐시에서 가져오기 때문에 member1과 member2는 항상 동일한 객체!!(동일성 같다 즉 실제 인스턴스가 같다!)

        System.out.println("findMember=" + findMember.getUsername() + ", age=" + findMember.getAge());

        List<Member> list = em.createQuery("select m from Member m", Member.class).getResultList(); // JPQL, SQL과 다르게 테이블을 대상으로 하는 것이 아닌 엔티티 객체를 대상으로 쿼리!!!
        //JPQL 실행 시에도 flush 자동 호출 -> 영속성 컨텍스트에만 저장된 엔티티들도 반영하기 위해서~

        System.out.println("members.size=" + list.size());

        em.remove(member); // 삭제 함수, 삭제 상태, 즉 엔티티 매니저가 소유한 영속성 컨텍스트와 데이터베이스에서 삭제됨!
        // 호출 시 영속성 컨텍스트에서는 바로 삭제됨(바로 즉시.)
    }
}
