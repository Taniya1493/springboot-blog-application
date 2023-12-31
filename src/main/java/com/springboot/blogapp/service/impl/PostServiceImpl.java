package com.springboot.blogapp.service.impl;

import com.springboot.blogapp.entity.Post;
import com.springboot.blogapp.exception.ResourceNotFoundException;
import com.springboot.blogapp.payload.PostDto;
import com.springboot.blogapp.payload.PostResponse;
import com.springboot.blogapp.repository.PostRepository;
import com.springboot.blogapp.service.PostService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private PostRepository postRepository;

    private ModelMapper mapper;

    public PostServiceImpl(PostRepository postRepository,ModelMapper mapper) {
        this.postRepository = postRepository;
        this.mapper=mapper;
    }


    @Override
    public PostDto createPost(PostDto postDto) {

        //convert Dto to entity
        Post post=convertDtoToEntity(postDto);

        Post newpost=postRepository.save(post);

        //convert entity to Dto
        //to show in response
        PostDto postResponse=convertEntityToDto(newpost);

        return postResponse;
    }

    @Override
    public PostResponse getAllPosts(int pageNo,int pageSize,String sortBy,String sortDir) {

        Sort sort=sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        //create Pageable instance
        PageRequest pageable= PageRequest.of(pageNo, pageSize,sort);

        Page<Post> posts=postRepository.findAll(pageable);

        //get content for page object
        List<Post> listOfPosts=posts.getContent();

        List<PostDto> content= listOfPosts.stream().map(post -> convertEntityToDto(post)).collect(Collectors.toList());

        PostResponse postResponse=new PostResponse();
        postResponse.setContent(content);
        postResponse.setPageNo(posts.getNumber());
        postResponse.setPageSize(posts.getSize());
        postResponse.setTotalElements(posts.getTotalElements());
        postResponse.setTotalPages(posts.getTotalPages());
        postResponse.setLast(posts.isLast());

        return postResponse;
    }

    @Override
    public PostDto getPostById(Long id) {
        Post post=postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post","id",id));
        return convertEntityToDto(post);
    }

    @Override
    public PostDto updatePost(PostDto postDto, Long id) {
        Post post=postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post","id",id));

        post.setTitle(postDto.getTitle());
        post.setDescription(postDto.getDescription());
        post.setContent(postDto.getContent());

        Post updatedPost=postRepository.save(post);
        return convertEntityToDto(updatedPost);
    }

    @Override
    public void deletePostById(Long id) {
        Post post=postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post","id",id));
        postRepository.delete(post);
    }


    private Post convertDtoToEntity(PostDto postDto){
        Post post=mapper.map(postDto,Post.class);
//        Post post=new Post();
//        post.setTitle(postDto.getTitle());
//        post.setContent(postDto.getContent());
//        post.setDescription(postDto.getDescription());

        return post;
    }

    private PostDto convertEntityToDto(Post post){
        PostDto postResponse=mapper.map(post,PostDto.class);
//        PostDto postResponse=new PostDto();
//        postResponse.setId(post.getId());
//        postResponse.setTitle(post.getTitle());
//        postResponse.setDescription(post.getDescription());
//        postResponse.setContent(post.getContent());

        return postResponse;
    }


}
