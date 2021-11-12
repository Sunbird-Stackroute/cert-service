import { Component, OnInit } from "@angular/core";
import { Router, ActivatedRoute } from "@angular/router";
import { CertVerifyService } from "../../services/cert-verify/cert-verify.service";
import * as $ from "jquery";
import { faShareAlt } from "@fortawesome/free-solid-svg-icons";
import {
	faLinkedin,
	faLinkedinIn,
	faInstagram,
	faFacebookF,
	faTwitter,
	faFacebookSquare,
	faTwitterSquare,
	faInstagramSquare,
} from "@fortawesome/free-brands-svg-icons";
import { Meta } from "@angular/platform-browser";

@Component({
  selector: 'app-cert-verify',
  templateUrl: './cert-verify.component.html',
  styleUrls: ['./cert-verify.component.scss']
})
export class CertificatesComponent implements OnInit {
	faShareAlt = faShareAlt;
	faLinkedin = faLinkedin;
	faLinkedinIn = faLinkedinIn;
	faFacebookSquare = faFacebookSquare;
	faTwitterSquare = faTwitterSquare;
	faInstagram = faInstagram;
	faFacebookF = faFacebookF;
	faTwitter = faTwitter;
	faInstagramSquare = faInstagramSquare;
	showSuccessModal;
	loader = false;
	viewCertificate: boolean;
	viewCertificateHtml: boolean;
	enableVerifyButton = true;
	certificateCode;
	certificateID;
	wrongCertificateCode = false;
	recipientName;
	courseName;
	courseDate;
	showCertCheck = true;
	showCertValid;
	showCertInvalid;

	constructor ( private router: Router, private certVerifyService: CertVerifyService, private meta: Meta, private activatedRoute: ActivatedRoute ) { }

	ngOnInit () {
		this.meta.addTags( [
			// LinkedIn, Facebook and others Meta Tags
			{
				property: "og:title",
				content: "NIIT Certified: Angular Master Class was issued by NIIT to Tanya Chauhan on 06/10/2021."
			},
			{
				property: "og:image",
				content: "https://rajudhami.me/certs/0b433995-e0b5-4794-976c-4f2948b42908"
			},
			{
				property: "og:description",
				content:
					"Angular is a TypeScript-based free and open-source web application framework led by the Angular Team at Google and by a community of individuals and corporations. Angular is a complete rewrite from the same team that built AngularJS."
			},
			{
				property: "og:type",
				content: "og:object"
			},
			{
				property: "og:url",
				content: "https://niit.com",
			},
			{
				property: "og:site_name",
				content: "NIIT",
			},
			// Twitter Meta Tags
			{
				name: "twitter:card",
				content: "summary_large_image",
			},
			{
				name: "twitter:site",
				content: "@NIITLtd",
			},
			{
				name: "twitter:domain",
				content: "niit.com",
			},
			{
				name: "twitter:creator",
				content: "@NIITLtd",
			},
			{
				name: "twitter:url",
				content: "http://niit.com",
			},
			{
				name: "twitter:title",
				content: "NIIT Certified: Angular Master Class was issued by NIIT to Tanya Chauhan on 06/10/2021."
			},
			{
				name: "twitter:description",
				content:
					"Angular is a TypeScript-based free and open-source web application framework led by the Angular Team at Google and by a community of individuals and corporations. Angular is a complete rewrite from the same team that built AngularJS."
			},
			{
				name: "twitter:image",
				content: "https://rajudhami.me/certs/0b433995-e0b5-4794-976c-4f2948b42908",
			}

		] );
	}

	getCodeLength ( event ) {
		const regex = /^[a-zA-Z0-9]+$/;
		this.wrongCertificateCode = false;
		if ( regex.test( event ) ) {
			this.certificateCode = event;
			this.certificateCode.length === 6 ? ( this.enableVerifyButton = false ) : ( this.enableVerifyButton = true );
		} else {
			this.wrongCertificateCode = true;
		}
	}

	async certificateVerify ( accessCode ) {
		this.enableVerifyButton = true;
		this.loader = true;
		const certId = this.router.url.split( "/" )[ 2 ];

		try {
			const response = await this.certVerifyService.certValidate( certId, accessCode );
			const {
				data: {
					result: {
						response: {
							json: {
								recipient: { name: recipientName },
								evidence: { name: courseName },
								issuedOn: courseDate,
							},
						},
					},
				},
			} = response;
			this.recipientName = recipientName;
			this.courseName = courseName;
			this.courseDate = courseDate.split( "T" )[ 0 ];
			this.showCertCheck = false;
			this.showCertValid = true;
		} catch ( error ) {
			console.error( error );
			this.certificateID = certId;
			this.showCertCheck = false;
			this.showCertInvalid = true;
		}
	}

	goBack () {
		$( "input[class='sb-form-control']" ).val( "" );
		this.loader = false;
		this.showCertCheck = true;
		this.showCertValid = false;
		this.showCertInvalid = false;
	}

	dynamicSocialShare ( data ) {
		console.log( data, window.location.href );
		// const url = window.location.href;
		const url = "https://www.3f07-110-235-238-130.ngrok.io";
		const res = {
			'linkedin': () => window.open( `https://www.linkedin.com/sharing/share-offsite/?url=${ url }`, "_blank" ),
			'facebook': () => window.open( `https://www.facebook.com/sharer/sharer.php?u=${ url }&quote=NIIT`, "_blank" ),
			'twitter': () => window.open( `https://twitter.com/intent/tweet?url=${ url }&text=NIIT`, "_blank" ),
		};
		res[ data ]();
	}
}
