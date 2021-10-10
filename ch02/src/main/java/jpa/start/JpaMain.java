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
            tx.commit();
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

        em.persist(member); // 저장 함수

        member.setAge(25); // JPA는 값이 변경되었는지 추적하는 기능을 갖고있음!! 따라서 값만 변경해주면 JPA가 알아서 UPDATE문을 작성해 반영한다

        Member findMember = em.find(Member.class, id);
        System.out.println("findMember=" + findMember.getUsername() + ", age=" + findMember.getAge());

        List<Member> list = em.createQuery("select m from Member m", Member.class).getResultList(); // JPQL, SQL과 다르게 테이블을 대상으로 하는 것이 아닌 엔티티 객체를 대상으로 쿼리!!!
        System.out.println("members.size=" + list.size());

        em.remove(member); // 삭제 함수
    }
}
