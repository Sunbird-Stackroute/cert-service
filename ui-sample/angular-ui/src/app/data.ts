export const builtInButtons = {
  cancel: {
      classes: 'sb-btn sb-btn-xs sb-btn-secondary cancel-button',
      text: 'Exit',
      type: 'cancel'
  },
  next: {
      classes: 'sb-btn sb-btn-xs sb-btn-secondary next-button',
      text: 'Next',
      type: 'next'
  },
  back: {
      classes: 'sb-btn sb-btn-xs sb-btn-outline-gray back-button',
      text: 'Back',
      type: 'back'
  },
  skip: {
      classes: 'sb-btn sb-btn-xs sb-btn-link sb-btn-link-tertiary skip-button',
      text: 'Skip',
      type: 'cancel'
  },
};

export const defaultStepOptions = {
  scrollTo: true,
  showCancelLink: false,
  modal: true,
  highlightClass: 'highlight',
  classes: 'shadow-md bg-purple-dark',
  id: 'testID1',
  tippyOptions: {
      duration: 500,
  },

};


export const steps = [
  {
      id: 'intro',
      useModalOverlay: true,
      options: {
          attachTo: '.tour-1 bottom',
          buttons: [
              builtInButtons.skip,
              builtInButtons.next
          ],
          classes: 'sb-guide-text-area',
          title: 'Import Contents',
          text: [`Lorem ipsum, or lipsum as it is sometimes known, is dummy text used in laying out print, graphic or web designs.`]
      }
  },
  {
      id: 'Library',
      useModalOverlay: true,
      options: {
          attachTo: '.tour-2 bottom',
          buttons: [
              builtInButtons.skip,
              builtInButtons.back,
              builtInButtons.next
          ],
          classes: 'sb-guide-text-area',
          title: 'Library',
          text: [`Lorem ipsum, or lipsum as it is sometimes known, is dummy text used in laying out print, graphic or web designs.`]
      }
  },
  {
      id: 'Enter QR Code',
      useModalOverlay: true,
      options: {
          attachTo: '.tour-3 bottom',
          buttons: [
              builtInButtons.back,
              builtInButtons.next
          ],
          classes: 'sb-guide-text-area',
          title: 'Enter QR Code',
          text: [`Enter QR code view this page`]
      }
  },
  {
      id: 'This is search section',
      useModalOverlay: true,
      options: {
          attachTo: '.tour-4 bottom',
          buttons: [
              builtInButtons.back,
              builtInButtons.next,
          ],
          classes: 'sb-guide-text-area',
          title: 'This is search section',
          text: [`This is search section where you can search for course and library`]
      }
  },

  {
      id: 'This is Language selection dropdown',
      useModalOverlay: true,
      options: {
          attachTo: '.tour-5 bottom',
          buttons: [
              builtInButtons.back,
              builtInButtons.cancel
          ],
          classes: 'sb-guide-text-area',
          title: 'This is Language selection dropdown',
          text: [`This is Language selection dropdown where you select the your langauge.`]
      }
  },
];
